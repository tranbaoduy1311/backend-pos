package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
@Data
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ingredientName;
    private String unit;
    private Double quantity;
    private Double totalCost;

    private LocalDate importDate;
    private LocalDateTime importTime;
    private Long supplierId;

    @PrePersist
    protected void onCreate() {
        importTime = LocalDateTime.now();
        if (isPaidDebt == null)
            isPaidDebt = true;
    }

    @Column(name = "is_paid_debt")
    private Boolean isPaidDebt;
}