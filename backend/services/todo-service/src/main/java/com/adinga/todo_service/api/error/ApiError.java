package com.adinga.todo_service.api.error;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;      // HTTP reason phrase (e.g. "Bad Request")
    private String message;    // 간단 요약
    private String path;       // 요청 경로
    private List<FieldError> errors = new ArrayList<>(); // 필드 단위 상세(옵션)

    public static class FieldError {
        private String field;
        private String code;       // 제약/에러 코드 (e.g. NotBlank)
        private String message;    // 사용자 메시지

        public FieldError() {}
        public FieldError(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }

        public String getField() { return field; }
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public void setField(String field) { this.field = field; }
        public void setCode(String code) { this.code = code; }
        public void setMessage(String message) { this.message = message; }
    }

    public ApiError() {}
    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldError> getErrors() { return errors; }

    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public void setStatus(int status) { this.status = status; }
    public void setError(String error) { this.error = error; }
    public void setMessage(String message) { this.message = message; }
    public void setPath(String path) { this.path = path; }
    public void setErrors(List<FieldError> errors) { this.errors = errors; }
}
