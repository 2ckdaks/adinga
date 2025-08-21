package com.adinga.todo_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTodoRequest {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @Size(max = 200, message = "제목은 최대 200자까지 가능합니다.")
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
