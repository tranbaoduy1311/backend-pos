package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "promotions")
@Data
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;
    private String discountType; // PERCENTAGE, FIXED
    private Double discountValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalTime startHour; // Happy Hour start
    private LocalTime endHour; // Happy Hour end

    private Double minOrderValue;
    private Double maxDiscountAmount;
    private String status; // ACTIVE, INACTIVE
}