package com.shop.api_gateway.service.impl;

import com.shop.api_gateway.entity.UserEntity;
import com.shop.api_gateway.entity.permission.UserServicePermissionEntity;
import com.shop.api_gateway.repository.UserPermissionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserPermissionsRepository userPermissionsRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserServicePermissionEntity> permissions = userPermissionsRepository.findValidPermissionByUsername(username);
        if (permissions.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        UserEntity user = permissions.get(0).getUser();
        List<GrantedAuthority> authorities;

            authorities = permissions.stream()
                    .filter(p ->  p.getPermission()!=null &&  p.getPermission().getServiceEntity().isActive())
                    .map(p -> new SimpleGrantedAuthority(new String(p.getPermission().getServiceEntity().getPathName() + p.getPermission().getPathPermission())))
                    .collect(Collectors.toList());

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
