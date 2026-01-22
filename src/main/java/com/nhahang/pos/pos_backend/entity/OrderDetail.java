package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_details")
@Data
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;

    // --- QUAN TRỌNG: BẠN ĐANG THIẾU DÒNG NÀY ---
    private String note;
}