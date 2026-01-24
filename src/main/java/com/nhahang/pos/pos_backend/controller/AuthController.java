package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Employee;
import com.nhahang.pos.pos_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    /**
     * Đăng nhập Admin bằng mã nhân viên
     * Chỉ cho phép nhân viên có chức vụ (Position) là ADMIN vào hệ thống
     */
    @PostMapping("/login-pos")
    public ResponseEntity<?> loginPos(@RequestParam String code) {
        // 1. Tìm nhân viên theo mã
        java.util.Optional<Employee> employeeOpt = employeeRepo.findByEmployeeCode(code);

        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();

            // 2. SỬA TẠI ĐÂY: Dùng getRole() thay vì getPosition()
            String empRole = emp.getRole();

            if (empRole != null && "ADMIN".equalsIgnoreCase(empRole)) {
                // Nếu là ADMIN -> Cho phép vào
                return ResponseEntity.ok(emp);
            } else {
                // Nếu tìm thấy mã nhưng Role không phải ADMIN
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Lỗi: Tài khoản này không có quyền truy cập khu vực Quản trị!");
            }
        } else {
            // Nếu không tìm thấy mã nhân viên
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Lỗi: Mã nhân viên không tồn tại!");
        }
    }
}