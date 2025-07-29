package com.shop.api_gateway.dto.enumDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumResult {
    USER_NOT_FOUND("user Not Found","100"),
    EMAIL_IS_USED("Email already in use","101"),
    PASSWORD_IS_NOT_MACH("password is not mach","102"),
    USER_IS_USED("Username already in use","101"),
    PHONE_IS_USED("PhoneNumber already in use","101"),
    ERROR_CREATE_ACCOUNT("Error creating account","500"),
    AUTHENTICATION_FAILED("Authentication failed","101"),
    BAD_CREDENTIALS("Bad credentials","400"),
    ACCOUNT_DISABLED("Account disabled","103"),
    FALLBACK_SERVICE("Fallback Service , Service is disabled","104"),
    INTERNAL_SERVER_ERROR("Internal Server Error","500"),
    PASSWORD_CHANGED("Password changed successfully","200"),
    BAD_REQUEST("Bad Request","400"),


    FORBIDDEN("Forbidden","403"),
    UNAUTHORIZED("Unauthorized","405"),

    ;



    private final String message;
    private final String code;
}
