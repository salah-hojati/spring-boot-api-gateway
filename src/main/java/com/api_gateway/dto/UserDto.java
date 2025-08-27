package com.api_gateway.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record UserDto(String username, String email, String firstName, String lastName, LocalDateTime lastLoginDate , Map<String, List<String>>  permissions) {
}
