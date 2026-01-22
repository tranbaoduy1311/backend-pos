package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dining_tables") // Đặt tên là dining_tables để tránh trùng từ khóa TABLE của SQL
@Data
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Tên bàn không được để trống và không trùng nhau
    private String name; // Ví dụ: "Bàn 01", "Bàn VIP"

    private String status;
    private String guestInfo;// Trạng thái: "Trống", "Có khách", "Đã đặt"
}