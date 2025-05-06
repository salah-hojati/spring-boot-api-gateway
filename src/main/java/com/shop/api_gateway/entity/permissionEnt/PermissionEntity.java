package com.shop.api_gateway.entity.permissionEnt;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "permissions")
public class PermissionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceEntity serviceEntity;

    @Column(nullable = false, unique = true, length = 50)
    private String pathPermission;


}
