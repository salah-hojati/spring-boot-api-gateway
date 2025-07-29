package com.shop.api_gateway.service;

import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.LoginResponseDto;
import com.shop.api_gateway.dto.ResponseDto;
import com.shop.api_gateway.dto.profile.CreateAccountRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {

    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest);

    CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto);

    void updateLastLoginInfo(UUID userId, String ipAddress);

    ResponseDto updatePassword(String newPassword, String currentPassword);


}
