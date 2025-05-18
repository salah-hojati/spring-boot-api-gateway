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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

import static com.shop.api_gateway.dto.enumDto.EnumResult.BAD_CREDENTIALS;
import static com.shop.api_gateway.dto.enumDto.EnumResult.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
@ControllerAdvice
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userService.login(loginRequestDto, request));
        }catch (BadCredentialsException e){
            return ResponseEntity.badRequest().body(new ErrResponseDto(BAD_CREDENTIALS));
        }
        catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrResponseDto(INTERNAL_SERVER_ERROR));
        }
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> sign(@Valid @RequestBody CreateAccountRequestDto requestDto) {
        try {
            CreateAccountResponseDto responseDto = userService.createAccount(requestDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (RecordException ex) {
            return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrResponseDto(INTERNAL_SERVER_ERROR));
        }
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ErrResponseDto>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrResponseDto> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ErrResponseDto(String.format("Error in field '%s': %s", fieldName, errorMessage), "400"));
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RecordException.class)
    public ResponseEntity<ErrResponseDto> handleRecordException(RecordException ex) {
        return new ResponseEntity<>(ex.getException(), ex.getHttpStatus());
    }

}
