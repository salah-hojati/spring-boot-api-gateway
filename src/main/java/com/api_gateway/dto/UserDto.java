package com.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(String username, String email, String firstName, String lastName, LocalDateTime lastLoginDate , List<String > permission) {
}
