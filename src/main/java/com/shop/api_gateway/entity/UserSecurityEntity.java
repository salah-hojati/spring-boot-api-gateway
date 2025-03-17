package com.shop.api_gateway.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_security")
public class UserSecurityEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String salt;

    @Column
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiryDate;

    @Column
    private String resetPasswordToken;

    @Column
    private LocalDateTime resetPasswordTokenExpiryDate;

    @Column
    private LocalDateTime accountLockoutExpiryDate;

    @Column
    private boolean twoFactorEnabled;

    @Column
    private String twoFactorSecret;

    @Column
    private LocalDateTime lastPasswordFailureDate;

    @Column
    private boolean accountLocked;

    @Column(length = 500)
    private String accountLockedReason;
}
