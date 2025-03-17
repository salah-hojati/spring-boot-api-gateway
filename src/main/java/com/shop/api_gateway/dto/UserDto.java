package com.shop.api_gateway.dto;

import java.time.LocalDateTime;

public record UserDto(String username, String email, String firstName, String lastName, LocalDateTime lastLoginDate) {
}
