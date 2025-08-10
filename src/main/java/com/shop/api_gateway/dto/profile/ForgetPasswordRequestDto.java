package com.shop.api_gateway.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ForgetPasswordRequestDto(

        @NotBlank(message = "contact cannot be blank")
        @Pattern(
                regexp = "^(?:\\d{11}|[a-zA-Z0-9._%+-]+@gmail\\.com)$",
                message = "Field must be either a valid 11-digit phone number or a Gmail address"
        )
        String contact) {
}
