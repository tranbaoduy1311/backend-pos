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

    // Dùng @Transient để hiển thị tên nhân viên khi query (không lưu vào DB)
    @Transient
    private String employeeName;
    @Transient
    private String employeeCode;

    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    // Khởi tạo mặc định là 0.0 để tránh lỗi NullPointerException khi tính toán
    private Double overtimeHours = 0.0;

    private String status; // WORKING, COMPLETED

    // --- THÊM HÀM NÀY ĐỂ SỬA LỖI BÊN PAYROLL CONTROLLER ---
    // Hàm này tự động tính số giờ làm dựa trên CheckIn và CheckOut
    public Double getWorkHours() {
        if (checkIn != null && checkOut != null) {
            // Tính khoảng cách giây giữa CheckIn và CheckOut
            long seconds = Duration.between(checkIn, checkOut).getSeconds();
            // Đổi ra giờ (Ví dụ: 3600s = 1 giờ)
            return seconds / 3600.0;
        }
        return 0.0; // Nếu chưa CheckOut thì coi như 0 giờ
    }
}