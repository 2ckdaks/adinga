package com.adinga.todo_service.api.dto;

import jakarta.validation.constraints.NotBlank;

public class TodoDtos {

    public static class CreateReq {
        @NotBlank
        public String title;
    }

    public static class UpdateReq {
        public String title;       // 부분 업데이트 허용
        public Boolean completed;  // 부분 업데이트 허용
    }

    public static class Res {
        public Long id;
        public String title;
        public boolean completed;
        public String createdAt;
        public String updatedAt;
    }
}
