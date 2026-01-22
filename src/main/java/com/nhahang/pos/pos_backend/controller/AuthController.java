package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Employee;
import com.nhahang.pos.pos_backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping("/login-pos")
    public Employee loginPos(@RequestParam String code) {
        // Tìm nhân viên theo mã
        return employeeRepo.findByEmployeeCode(code)
                .orElseThrow(() -> new RuntimeException("Mã nhân viên không tồn tại!"));
    }
}