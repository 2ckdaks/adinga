package com.adinga.todo_service.api;

import com.adinga.todo_service.api.dto.TodoDtos;
import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService service;
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TodoDtos.Res> list() {
        return service.findAll().stream().map(this::toRes).toList();
    }

    @GetMapping("/{id}")
    public TodoDtos.Res get(@PathVariable Long id) {
        return toRes(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoDtos.Res create(@Valid @RequestBody TodoDtos.CreateReq req) {
        return toRes(service.create(req.title));
    }

    @PatchMapping("/{id}")
    public TodoDtos.Res patch(@PathVariable Long id, @RequestBody TodoDtos.UpdateReq req) {
        return toRes(service.updatePartial(id, req.title, req.completed));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private TodoDtos.Res toRes(Todo t) {
        TodoDtos.Res r = new TodoDtos.Res();
        r.id = t.getId();
        r.title = t.getTitle();
        r.completed = t.isCompleted();
        r.createdAt = t.getCreatedAt() != null ? fmt.format(t.getCreatedAt()) : null;
        r.updatedAt = t.getUpdatedAt() != null ? fmt.format(t.getUpdatedAt()) : null;
        return r;
    }
}
