package com.nhahang.pos.pos_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Duration; // Nhớ import thư viện này để tính khoảng cách thời gian
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Data
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @Transient
    private String employeeName;
    @Transient
    private String employeeCode;

    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private Double overtimeHours = 0.0;

    private String status;

    public Double getWorkHours() {
        if (checkIn != null && checkOut != null) {
            long seconds = Duration.between(checkIn, checkOut).getSeconds();
            return seconds / 3600.0;
        }
        return 0.0;
    }
}