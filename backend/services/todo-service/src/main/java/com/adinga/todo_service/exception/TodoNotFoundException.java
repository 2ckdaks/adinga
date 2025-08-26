package com.adinga.todo_service.exception;

public class TodoNotFoundException extends RuntimeException {
    private final Long id;

    public TodoNotFoundException(Long id) {
        super("Todo not found: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
