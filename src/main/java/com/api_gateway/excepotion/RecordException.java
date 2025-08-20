package com.api_gateway.excepotion;

import com.api_gateway.dto.ResponseDto;
import com.api_gateway.dto.enumDto.EnumResult;
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

    public RecordException(EnumResult result , HttpStatus httpStatus) {
        super(result.name());
        this.message = result.getMessage();
        this.code = result.getCode();
        this.httpStatus = httpStatus;
    }

    public ResponseDto getException() {
        return new ResponseDto(code, message);
    }

}
