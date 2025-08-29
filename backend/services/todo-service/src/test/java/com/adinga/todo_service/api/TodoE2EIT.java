package com.adinga.todo_service.api;

import com.adinga.todo_service.api.dto.CreateTodoRequest;
import com.adinga.todo_service.domain.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoE2EIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    String baseUrl() { return "http://localhost:" + port + "/todos"; }

    @Test
    void create_then_get_by_id() {
        // 1) POST /todos
        CreateTodoRequest req = new CreateTodoRequest();
        req.setTitle("E2E - 만들기");

        ResponseEntity<Todo> createRes =
                rest.postForEntity(baseUrl(), req, Todo.class);

        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = createRes.getHeaders().getLocation().toString();
        assertThat(location).contains("/todos/");

        // 2) GET {Location}
        ResponseEntity<Todo> getRes =
                rest.getForEntity(location, Todo.class);

        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody().getTitle()).isEqualTo("E2E - 만들기");
    }

    @Test
    void validation_error_returns_400() {
        // title 비워서 전송
        CreateTodoRequest bad = new CreateTodoRequest();
        bad.setTitle("  "); // @NotBlank 위반

        ResponseEntity<String> res =
                rest.postForEntity(baseUrl(), bad, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // 공통 에러 포맷(GlobalExceptionHandler) 일부 키 존재 확인
        assertThat(res.getBody()).contains("validation_failed");
    }
}
