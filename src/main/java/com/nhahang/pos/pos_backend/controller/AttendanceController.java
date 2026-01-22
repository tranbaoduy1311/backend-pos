package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.*;
import com.nhahang.pos.pos_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepo;
    @Autowired
    private EmployeeRepository employeeRepo;

    // 1. Lấy danh sách chấm công (Kèm thông tin nhân viên)
    @GetMapping
    public List<Attendance> getAll() {
        List<Attendance> list = attendanceRepo.findAllByOrderByCheckInDesc();
        // Điền tên nhân viên vào
        list.forEach(a -> {
            employeeRepo.findById(a.getEmployeeId()).ifPresent(e -> {
                a.setEmployeeName(e.getName());
                a.setEmployeeCode(e.getEmployeeCode());
            });
        });
        return list;
    }

    // 2. Check-in (Vào ca)
    @PostMapping("/check-in")
    public Attendance checkIn(@RequestParam Long employeeId) {
        // Kiểm tra xem nhân viên này đang làm việc chưa
        if (attendanceRepo.findByEmployeeIdAndStatus(employeeId, "WORKING").isPresent()) {
            throw new RuntimeException("Nhân viên này chưa Check-out ca trước!");
        }

        Attendance att = new Attendance();
        att.setEmployeeId(employeeId);
        att.setWorkDate(LocalDate.now());
        att.setCheckIn(LocalDateTime.now());
        att.setStatus("WORKING");
        att.setOvertimeHours(0.0);
        return attendanceRepo.save(att);
    }

    // 3. Check-out (Tan ca)
    @PostMapping("/check-out")
    public Attendance checkOut(@RequestParam Long employeeId, @RequestParam(defaultValue = "0") Double overtime) {
        Attendance att = attendanceRepo.findByEmployeeIdAndStatus(employeeId, "WORKING")
                .orElseThrow(() -> new RuntimeException("Nhân viên này chưa Check-in!"));

        att.setCheckOut(LocalDateTime.now());
        att.setStatus("COMPLETED");
        att.setOvertimeHours(overtime); // Lưu giờ tăng ca nếu có
        return attendanceRepo.save(att);
    }
}