package com.shop.api_gateway.service.impl;

import com.shop.api_gateway.dto.LoginRequestDto;
import com.shop.api_gateway.dto.LoginResponseDto;
import com.shop.api_gateway.dto.UserDto;
import com.shop.api_gateway.dto.profile.CreateAccountRequestDto;
import com.shop.api_gateway.dto.profile.CreateAccountResponseDto;
import com.shop.api_gateway.entity.UserEntity;
import com.shop.api_gateway.entity.UserSecurityEntity;
import com.shop.api_gateway.excepotion.RecordException;
import com.shop.api_gateway.repository.UserRepository;
import com.shop.api_gateway.repository.UserSecurityRepository;
import com.shop.api_gateway.service.UserService;
import com.shop.api_gateway.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, String ip) {
        String redisKey = "user:"+loginRequestDto.username();
        String ipRedisKey =  "ip:"+ip;
        Integer failedAttempts = 0;
        Integer ipFailedAttempts = 0;
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
                claims.put("permissions", permissions);

                String token = "Bearer " + jwtUtil.generateToken(userDetails, claims);

                UserEntity user = userRepository.findByUsername(loginRequestDto.username()).get();
                UserDto userDto = new UserDto(user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getLastLoginDate());
                updateLastLoginInfo(user.getId(),ip);
                redisService.resetFailedAttempts(redisKey);
                return new LoginResponseDto(token, userDto);
            } else {
                redisService.incrementFailedAttempts(redisKey);
                throw new RecordException("Authentication failed", "101", HttpStatus.UNAUTHORIZED);
            }
        } catch (BadCredentialsException e) {
            redisService.incrementFailedAttempts(redisKey);
            redisService.incrementFailedAttempts(ipRedisKey);
            throw new RecordException("Bad credentials", "101", HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException("Account disabled", "101", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            if (e instanceof RecordException) {
                throw e;
            }
            redisService.incrementFailedAttempts(redisKey);
            throw new RecordException("Internal server error", "500", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public CreateAccountResponseDto createAccount(CreateAccountRequestDto requestDto) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String createdByUsername = userDetails.getUsername();
            UserEntity createdByUser = userRepository.findByUsername(createdByUsername).orElse(null);

            if (createdByUser == null) {
                log.error("User not found: {}", createdByUsername);
                throw new RecordException("User not found", "USER_NOT_FOUND",HttpStatus.NOT_FOUND);
            }

           if (userRepository.existsByEmail(requestDto.email())){
               throw new RecordException("Email already in use", "101", HttpStatus.CONFLICT);
           }
           if (userRepository.existsByUsername(requestDto.username())){
               throw new RecordException("Username already in use", "101", HttpStatus.CONFLICT);
           }
           if (userRepository.existsByPhoneNumber(requestDto.phoneNumber())){
             throw new RecordException("PhoneNumber already in use","101", HttpStatus.CONFLICT);
           }

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

            newUser = userRepository.save(newUser);

            UserSecurityEntity userSecurity = new UserSecurityEntity();
            userSecurity.setUserId(newUser.getId());
            userSecurity.setSalt("salt");
            userSecurityRepository.save(userSecurity);

            UserDto userDto = new UserDto( newUser.getUsername(), newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getLastLoginDate());
            return new CreateAccountResponseDto(userDto);
        } catch (Exception e) {
            log.error("Error creating account: " + e.getMessage());
            throw new RecordException("Error creating account", "CREATE_ACCOUNT_ERROR",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public void updateLastLoginInfo(UUID userId, String ipAddress) {
        userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), ipAddress);
    }

    @Override
    public void updatePassword(UUID userId, String password) {
        userRepository.updatePassword(userId, passwordEncoder.encode(password));
    }

    @Override
    public void updateLastPasswordResetDate(UUID userId) {
        userRepository.updateLastPasswordResetDate(userId, LocalDateTime.now());
    }


}
