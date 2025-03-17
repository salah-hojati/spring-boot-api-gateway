package com.shop.api_gateway.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 10)
    private String phoneNumber;

    @Column
    private LocalDateTime lastLoginDate;

    @Column(nullable = false)
    private boolean enabled;

    @Version
    private Long version;

    @Column
    private String lastIp;

    @Column
    private LocalDateTime lastPasswordResetDate;

    @Column
    private LocalDateTime CreatedDate;

    @Column
    private UUID createdBy;

}
