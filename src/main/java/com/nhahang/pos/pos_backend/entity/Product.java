package com.nhahang.pos.pos_backend.entity; // Đã sửa theo package của bạn

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String image;
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}