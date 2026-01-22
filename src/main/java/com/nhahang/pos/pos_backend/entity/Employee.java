package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employeeCode;
    private String name;
    private String role;
    private String phone;
    private Double salary;
    private LocalDate startDate;

    // ... trong class Employee
    private String salaryType; // HOURLY, MONTHLY
    private Double hourlyRate; // Lương theo giờ
}