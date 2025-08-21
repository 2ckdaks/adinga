package com.adinga.todo_service.api.dto;

import com.adinga.todo_service.validation.NotBlankIfPresent;
import jakarta.validation.constraints.Size;

public class UpdateTodoRequest {

    // 선택 필드: 주어지면 공백 불가, 길이 제한
    @NotBlankIfPresent(message = "제목이 비어 있을 수 없습니다.")
    @Size(max = 200, message = "제목은 최대 200자까지 가능합니다.")
    private String title;       // null이면 변경 안 함

    private Boolean completed;  // null이면 변경 안 함

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
