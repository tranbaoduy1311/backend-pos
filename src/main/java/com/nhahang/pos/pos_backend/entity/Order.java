package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tableId;
    private Long customerId;

    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;

    private String voucherCode;
    private Double discountAmount = 0.0;
    private Double finalPrice;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (finalPrice == null) {
            finalPrice = totalPrice != null ? totalPrice : 0.0;
        }
    }
}