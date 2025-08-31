package com.api_gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDto(
        @Schema(description = "JWT Bearer token")
        String token,
        @Schema(description = "User information")
        UserDto user) {
}
