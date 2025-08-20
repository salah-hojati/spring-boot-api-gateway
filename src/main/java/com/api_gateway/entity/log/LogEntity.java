package com.api_gateway.entity.log;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "logs")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime timestamp;

    @Column(length = 50)
    private String logLevel;

    @Column(length = 100)
    private String serviceName;

    @Column(length = 255)
    private String operation;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    @Lob
    @Column
    private String message;

    @Lob
    @Column
    private String stackTrace;


}
