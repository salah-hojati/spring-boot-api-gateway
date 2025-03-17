package com.shop.api_gateway.service.impl;

import com.shop.api_gateway.entity.UserEntity;
import com.shop.api_gateway.repository.PermissionRepository;
import com.shop.api_gateway.repository.UserPermissionsRepository;
import com.shop.api_gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserPermissionsRepository userPermissionsRepository;
    private final PermissionRepository permissionRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        UserEntity user = userOptional.get();

        List<GrantedAuthority> authorities = userPermissionsRepository.findByUserId(user.getId()).stream()
                .map(userPermission -> permissionRepository.findById(userPermission.getPermission().getId()).orElse(null))
                .filter(permission -> permission != null)
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
