package com.shop.api_gateway.dto;

import com.shop.api_gateway.dto.enumDto.EnumResult;

public record ErrResponseDto(String message, String code) {

    public ErrResponseDto(EnumResult enumResult){
        this(enumResult.getMessage(), enumResult.getCode());
    }

    public ErrResponseDto(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
