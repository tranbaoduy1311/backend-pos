package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByMonthAndYear(int month, int year);

    void deleteByMonthAndYear(int month, int year); // Dùng để tính lại lương
}