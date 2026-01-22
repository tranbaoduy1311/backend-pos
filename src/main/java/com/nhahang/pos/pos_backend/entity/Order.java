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
    private Long customerId;// <--- QUAN TRỌNG: Thêm dòng này để lưu ID bàn

    private Double totalPrice;
    private String status; // PENDING, PAID
    private LocalDateTime createdAt;

    private String voucherCode;
    private Double discountAmount = 0.0; // Mặc định 0
    private Double finalPrice; // Giá sau khi giảm

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (finalPrice == null) {
            finalPrice = totalPrice != null ? totalPrice : 0.0;
        }
    }
}