package com.shop.api_gateway.dto;

public record LoginResponseDto(String token, UserDto user) {
}
