package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dining_tables")
@Data
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String status;
    private String guestInfo;
}