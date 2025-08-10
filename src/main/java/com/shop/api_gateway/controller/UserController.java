package com.shop.api_gateway.controller;

import com.shop.api_gateway.dto.ResponseDto;
import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.profile.*;
import com.shop.api_gateway.excepotion.RecordException;
import com.shop.api_gateway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.shop.api_gateway.dto.enumDto.EnumResult.BAD_CREDENTIALS;
import static com.shop.api_gateway.dto.enumDto.EnumResult.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@ControllerAdvice
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userService.login(loginRequestDto, request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ResponseDto(BAD_CREDENTIALS));
        } catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto(INTERNAL_SERVER_ERROR));
        }
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> sign(@Valid @RequestBody CreateAccountRequestDto requestDto) {
        try {
            CreateAccountResponseDto responseDto = userService.createAccount(requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto(INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
        try {
            return ResponseEntity.ok(userService.updatePassword(updatePasswordRequestDto.newPassword(), updatePasswordRequestDto.currentPassword()));
        } catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto(INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody ForgetPasswordRequestDto forgetPasswordRequestDto) {
        try {
            return ResponseEntity.ok(userService.forgotPassword(forgetPasswordRequestDto.contact()));
        } catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto(INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        try {
            return ResponseEntity.ok(userService.resetPassword(resetPasswordRequestDto.token(), resetPasswordRequestDto.newPassword()));
        } catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDto(INTERNAL_SERVER_ERROR));
        }
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ResponseDto>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ResponseDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ResponseDto(String.format("Error in field '%s': %s", fieldName, errorMessage), "400"));
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RecordException.class)
    public ResponseEntity<ResponseDto> handleRecordException(RecordException ex) {
        return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
    }

}
