package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@Data
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String employeeName;
    private int month;
    private int year;

    private String salaryType;
    private Double baseSalary;

    private Integer totalWorkDays;
    private Double totalWorkHours;
    private Double overtimeHours;

    private Double bonus = 0.0;
    private Double deduction = 0.0;
    private Double totalSalary;
    private String status;

    private LocalDateTime createdAt;

    private Integer standardWorkDays;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}