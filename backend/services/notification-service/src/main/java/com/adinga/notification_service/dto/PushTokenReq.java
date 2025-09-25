package com.adinga.notification_service.dto;

import jakarta.validation.constraints.NotBlank;

public record PushTokenReq(
        @NotBlank String deviceId,
        @NotBlank String expoToken
) {}
