package com.adinga.todo_service.repository;

import com.adinga.todo_service.domain.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TodoRepositoryIT {

    // MySQL 컨테이너 한 번 띄우고 전체 테스트 동안 재사용
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36");

    @Autowired
    private TodoRepository repo;

    @Test
    void save_and_find() {
        Todo t = new Todo();
        t.setTitle("통합 테스트 - 저장");
        Todo saved = repo.save(t);

        assertThat(saved.getId()).isNotNull();

        Todo found = repo.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("통합 테스트 - 저장");
        assertThat(found.getCreatedAt()).isNotNull(); // 컬럼/감사 필드 검증 등
    }
}
