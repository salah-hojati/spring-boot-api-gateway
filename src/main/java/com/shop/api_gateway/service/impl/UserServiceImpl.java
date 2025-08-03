package com.shop.api_gateway.service.impl;

import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.LoginResponseDto;
import com.shop.api_gateway.dto.ResponseDto;
import com.shop.api_gateway.dto.UserDto;
import com.shop.api_gateway.dto.profile.CreateAccountRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountResponseDto;
import com.shop.api_gateway.entity.UserEntity;
import com.shop.api_gateway.entity.UserSecurityEntity;
import com.shop.api_gateway.entity.permissionEnt.UserServicePermissionEntity;
import com.shop.api_gateway.entity.profile.UserProfileEntity;
import com.shop.api_gateway.excepotion.RecordException;
import com.shop.api_gateway.repository.UserPermissionsRepository;
import com.shop.api_gateway.repository.UserProfileRepository;
import com.shop.api_gateway.repository.UserRepository;
import com.shop.api_gateway.repository.UserSecurityRepository;
import com.shop.api_gateway.service.UserService;
import com.shop.api_gateway.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.shop.api_gateway.dto.enumDto.EnumResult.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserPermissionsRepository userPermissionsRepository;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher publisher;



    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        String redisKey = "user:" + loginRequestDto.username();
        String ipRedisKey = "ip:" + request.getRemoteAddr();
        Integer failedAttempts;
        Integer ipFailedAttempts;
        try {
            failedAttempts = redisService.getFailedAttempts(redisKey);
            ipFailedAttempts = redisService.getFailedAttempts(ipRedisKey);
        } catch (Exception e) {
            log.error("exception Redis server error :", e);
            throw new RecordException("Internal server error", "500", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            if (failedAttempts >= 5 || ipFailedAttempts >= 5) {
                throw new RecordException("Account or Ip Locked", "101", HttpStatus.LOCKED);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password()));

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                Map<String, Object> claims = new HashMap<>();

                List<String> permissions = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                UserEntity user = userRepository.findByUsername(loginRequestDto.username()).get();

                claims.put("permissions", permissions);
                String uuid = UUID.randomUUID().toString();

                claims.put("jti", uuid);
                String deviceId = (request.getHeader("User-Agent") != null) ? request.getHeader("User-Agent") : "unknown";
                redisService.setDeviceIdForJti(uuid, deviceId);
                String token = "Bearer " + jwtUtil.generateToken(userDetails, claims);

                UserDto userDto = new UserDto(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getLastLoginDate());
                updateLastLoginInfo(user.getId(), request.getRemoteAddr());
                redisService.resetFailedAttempts(redisKey);
                return new LoginResponseDto(token, userDto);
            } else {
                redisService.incrementFailedAttempts(redisKey);
                throw new RecordException(AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            redisService.incrementFailedAttempts(redisKey);
            redisService.incrementFailedAttempts(ipRedisKey);
            throw new RecordException(BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException(ACCOUNT_DISABLED, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            if (e instanceof RecordException) {
                throw e;
            }
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String createdByUsername = userDetails.getUsername();
            UserEntity createdByUser = userRepository.findByUsername(createdByUsername).orElse(null);

            if (createdByUser == null)
                throw new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);

            if (userRepository.existsByEmail(requestDto.email()))
                throw new RecordException(EMAIL_IS_USED, HttpStatus.CONFLICT);

            if (userRepository.existsByUsername(requestDto.username()))
                throw new RecordException(USER_IS_USED, HttpStatus.CONFLICT);

            if (userRepository.existsByPhoneNumber(requestDto.phoneNumber()))
                throw new RecordException(PHONE_IS_USED, HttpStatus.CONFLICT);

            UserEntity newUser = new UserEntity();
            newUser.setUsername(requestDto.username());
            newUser.setPassword(passwordEncoder.encode(requestDto.password()));
            newUser.setEmail(requestDto.email());
            newUser.setFirstName(requestDto.firstName());
            newUser.setLastName(requestDto.lastName());
            newUser.setCreatedBy(createdByUser.getId());
            newUser.setEnabled(true);
            newUser.setCreatedDate(LocalDateTime.now());
            newUser.setLastPasswordResetDate(LocalDateTime.now());
            newUser.setLastLoginDate(LocalDateTime.now());

            newUser = userRepository.saveAndFlush(newUser);

            UserProfileEntity userProfileEntity = new UserProfileEntity();
            userProfileEntity.setUserId(newUser.getId());

            UserSecurityEntity userSecurity = new UserSecurityEntity();
            userSecurity.setUserId(newUser.getId());
            userSecurity.setSalt("salt");

            UserServicePermissionEntity userServicePermissionEntity = new UserServicePermissionEntity();
            userServicePermissionEntity.setUser(newUser);
            //can handle Expire Date
            userServicePermissionEntity.setEndDate(LocalDateTime.now().plusYears(1L));
            userServicePermissionEntity.setGrantedBy(createdByUser.getId());
            userPermissionsRepository.save(userServicePermissionEntity);

            userProfileRepository.save(userProfileEntity);
            userSecurityRepository.save(userSecurity);

            return new CreateAccountResponseDto(new UserDto(newUser.getUsername(), newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getLastLoginDate()));
        } catch (Exception e) {
            if (e instanceof RecordException) throw e;
            log.error("Error creating account: {}", e.getMessage());
            throw new RecordException(ERROR_CREATE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateLastLoginInfo(UUID userId, String ipAddress) {
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), ipAddress);
    }

    @Override
    public ResponseDto updatePassword(String newPassword, String currentPassword) {

        if (newPassword.equals(currentPassword))
            throw new RecordException(BAD_REQUEST, HttpStatus.BAD_REQUEST);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String createdByUsername = userDetails.getUsername();
        UserEntity createdByUser = userRepository.findByUsername(createdByUsername).stream().findFirst().orElse(null);

        if (createdByUser == null)
            throw new RecordException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);

        log.info("Starting password update for userId: {}", createdByUser.getUsername());

        if (!passwordEncoder.matches(currentPassword, createdByUser.getPassword())) {
            log.warn("Password mismatch for userId: {}", createdByUser.getUsername());
            throw new RecordException(PASSWORD_IS_NOT_MACH, HttpStatus.BAD_REQUEST);
        }
        log.info("Password successfully updated for userId: {}", createdByUser.getUsername());
        try {
            userRepository.updatePassword(createdByUser.getId(), LocalDateTime.now(), passwordEncoder.encode(newPassword));
        } catch (Exception e) {
            log.error("Error updating password for userId: {}", createdByUser.getUsername());
            throw new RecordException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseDto(PASSWORD_CHANGED);
    }

    @Override
    public ResponseDto forgotPassword(String contact) {

        return null;
    }
}
