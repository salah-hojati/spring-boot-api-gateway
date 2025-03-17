package com.shop.api_gateway.controller;

import com.shop.api_gateway.dto.ErrResponseDto;
import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountResponseDto;
import com.shop.api_gateway.excepotion.RecordException;
import com.shop.api_gateway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@ControllerAdvice
public class UserController {

    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userService.login(loginRequestDto, request.getRemoteAddr()));
        } catch (RecordException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getException());
        }
    }


    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequestDto requestDto) {
        try {
            CreateAccountResponseDto responseDto = userService.createAccount(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (RecordException e) {
            return ResponseEntity.badRequest().body(e.getException());
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrResponseDto>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrResponseDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ErrResponseDto(String.format("Error in field '%s': %s", fieldName, errorMessage), "400"));
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
