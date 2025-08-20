package com.api_gateway.entity.permission;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500, nullable = false)
    private String pathName;

    @Column
    private boolean active;

    @Version
    private Long version;

}
