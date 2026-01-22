package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ingredients")
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String unit;
    private Double quantity;
    private Double costPrice;
}