package com.api_gateway.dto.profile;

import jakarta.validation.constraints.*;

public record CreateAccountRequestDto(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 50, message = "First name cannot be longer than 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 50, message = "Last name cannot be longer than 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
        String lastName
        ,

        @NotBlank(message = "phoneNumber cannot be blank")
        @Size(max = 10,min = 10, message = "phone Number should be 10 number")
        @Pattern(regexp = "\\d{10}", message = "phone Number should be 10 number")
        String phoneNumber
) {
}