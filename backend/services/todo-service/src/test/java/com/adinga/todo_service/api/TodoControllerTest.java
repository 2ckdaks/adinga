package com.adinga.todo_service.api;

import com.adinga.todo_service.api.dto.CreateTodoRequest;
import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.exception.TodoNotFoundException;
import com.adinga.todo_service.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean TodoService todoService;

    private Todo sample(Long id, String title, boolean completed) {
        Todo t = new Todo();
        t.setId(id); t.setTitle(title); t.setCompleted(completed);
        t.setCreatedAt(OffsetDateTime.now()); t.setUpdatedAt(OffsetDateTime.now());
        return t;
    }

    @Test
    void post_valid_returns201_andLocation() throws Exception {
        Todo created = sample(1L, "hello", false);
        when(todoService.create("hello")).thenReturn(created);

        mvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"title":"hello"}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/todos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("hello"));
    }

    @Test
    void post_invalid_blankTitle_returns400_withValidationBody() throws Exception {
        mvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"title":""}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.details[0].field").value("title"));
    }

    @Test
    void get_byId_notFound_returns404_withErrorBody() throws Exception {
        when(todoService.findById(999L)).thenThrow(new TodoNotFoundException(999L));

        mvc.perform(get("/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", anyOf(equalTo("not_found"), equalTo("NOT_FOUND"))).exists());
        // 에러 포맷이 {"error":"not_found","message":...} 라면 위 expect가 통과
    }

    @Test
    void patch_blankTitle_returns400() throws Exception {
        mvc.perform(patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"title":"   "}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"));
    }

    @Test
    void patch_completedOnly_returns200_withUpdated() throws Exception {
        when(todoService.updatePartial(eq(1L), isNull(), eq(true)))
                .thenReturn(sample(1L, "keep", true));

        mvc.perform(patch("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{"completed":true}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("keep"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void list_withPageable_returnsPageJson() throws Exception {
        Page<Todo> page = new PageImpl<>(
                List.of(sample(1L, "a", false)),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                1
        );
        when(todoService.findAll(any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }
}
