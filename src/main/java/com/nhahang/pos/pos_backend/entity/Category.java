package com.nhahang.pos.pos_backend.entity; // Đã sửa theo package của bạn

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}