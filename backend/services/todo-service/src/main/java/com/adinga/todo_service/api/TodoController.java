package com.adinga.todo_service.api;

import com.adinga.todo_service.api.dto.CreateTodoRequest;
import com.adinga.todo_service.api.dto.UpdateTodoRequest;
import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public Page<Todo> list(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return todoService.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Todo> create(@Valid @RequestBody CreateTodoRequest req) {
        Todo created = todoService.create(req.getTitle());
        return ResponseEntity.created(URI.create("/todos/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public Todo get(@PathVariable Long id) {
        return todoService.findById(id);
    }

    @PatchMapping("/{id}")
    public Todo patch(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest req) {
        return todoService.updatePartial(id, req.getTitle(), req.getCompleted());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable long id) {
        todoService.toggle(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }
}
