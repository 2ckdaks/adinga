package com.adinga.todo_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null -> 미전달(검증 X), 값이 오면 공백문자만은 허용하지 않음
        return value == null || !value.trim().isEmpty();
    }
}
