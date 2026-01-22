package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phone;
    private Integer guestCount;
    private LocalDateTime reservationTime;
    private String status;
}