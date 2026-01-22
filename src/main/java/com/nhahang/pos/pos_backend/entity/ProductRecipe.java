package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_ingredients") // Tên bảng trong MySQL
@Data
public class ProductRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    // --- QUAN TRỌNG: PHẢI CÓ ĐOẠN NÀY ĐỂ LẤY TÊN NGUYÊN LIỆU ---
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private Double quantityRequired;
}