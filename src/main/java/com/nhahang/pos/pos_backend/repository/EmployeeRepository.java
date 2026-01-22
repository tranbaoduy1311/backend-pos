package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // <--- QUAN TRỌNG: Phải có dòng này mới dùng được Optional

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Tìm kiếm theo chức vụ (Ví dụ: Lấy danh sách Đầu bếp)
    List<Employee> findByRole(String role);

    // Tìm kiếm theo mã nhân viên (Dùng cho đăng nhập POS)
    Optional<Employee> findByEmployeeCode(String employeeCode);
}