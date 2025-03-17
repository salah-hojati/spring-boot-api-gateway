package com.shop.api_gateway.entity.profile;

import com.shop.api_gateway.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {

    @Id
    @Column()
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private LocalDate birthDate;

    @Column(length = 500)
    private String bio;

    @Lob
    @Column
    private byte[] profilePicture;

    @Embedded
    private AddressEmbed address;

    @Column(length = 100)
    private String company;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String jobTitle;
}
