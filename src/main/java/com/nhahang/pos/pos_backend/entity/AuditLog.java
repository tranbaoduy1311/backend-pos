package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String description;
    private String actor;
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}