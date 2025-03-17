package com.shop.api_gateway.excepotion;

import com.shop.api_gateway.dto.ErrResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RecordException extends RuntimeException {

    private final String message;
    private final String code;
    @Getter
    private final HttpStatus httpStatus;

    public RecordException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public ErrResponseDto getException() {
        return new ErrResponseDto(code, message);
    }

}
