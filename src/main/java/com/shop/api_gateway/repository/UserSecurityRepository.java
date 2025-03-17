package com.shop.api_gateway.repository;

import com.shop.api_gateway.entity.UserEntity;
import com.shop.api_gateway.entity.UserSecurityEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSecurityRepository extends JpaRepository<UserSecurityEntity, UUID> {


    Optional<UserSecurityEntity> findByVerificationToken(String verificationToken);

    Optional<UserSecurityEntity> findByResetPasswordToken(String resetPasswordToken);

}

