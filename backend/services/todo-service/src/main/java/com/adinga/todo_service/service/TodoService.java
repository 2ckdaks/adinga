package com.adinga.todo_service.service;

import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public List<Todo> findAll() {
        return repo.findAll();
    }

    public Todo findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Todo not found: " + id));
    }

    public Todo create(String title) {
        Todo t = new Todo();
        t.setTitle(title);
        return repo.save(t);
    }

    public Todo updatePartial(Long id, String title, Boolean completed) {
        Todo t = findById(id);
        if (title != null) t.setTitle(title);
        if (completed != null) t.setCompleted(completed);
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
