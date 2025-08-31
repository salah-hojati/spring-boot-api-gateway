package com.api_gateway.controller;

import com.api_gateway.dto.LoginRequestDto;
import com.api_gateway.dto.LoginResponseDto;
import com.api_gateway.dto.ResponseDto;
import com.api_gateway.dto.profile.*;
import com.api_gateway.excepotion.RecordException;
import com.api_gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static com.api_gateway.dto.enumDto.EnumResult.BAD_CREDENTIALS;
import static com.api_gateway.dto.enumDto.EnumResult.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@ControllerAdvice
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token with permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad credentials or validation error", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "423", description = "Account locked", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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


    @Operation(
            summary = "Sign up user",
            description = "Create a new user account. Requires an authenticated manager user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = CreateAccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Manager user not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict - Email/Username/Phone already used",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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


    @Operation(
            summary = "Update user password",
            description = "Allows an authenticated user to update their password. New password must be different from current password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, password mismatch or invalid input",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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


    @Operation(
            summary = "Forgot password",
            description = "Initiate password reset process. Contact can be either a Gmail address or 11-digit phone number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset initiated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "406", description = "Account locked due to too many attempts",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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


    @Operation(
            summary = "Reset password",
            description = "Reset the password using a valid token received via forget password flow."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Token not found or expired",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
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
