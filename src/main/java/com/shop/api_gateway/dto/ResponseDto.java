package com.shop.api_gateway.dto;

import com.shop.api_gateway.dto.enumDto.EnumResult;

public record ResponseDto(String message, String code) {

    public ResponseDto(EnumResult enumResult){
        this(enumResult.getMessage(), enumResult.getCode());
    }

    public ResponseDto(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
