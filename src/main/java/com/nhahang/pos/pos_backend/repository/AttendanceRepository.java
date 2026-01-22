package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Tìm xem nhân viên này hôm nay đã check-in chưa (mà chưa check-out)
    Optional<Attendance> findByEmployeeIdAndStatus(Long employeeId, String status);

    // Lấy lịch sử chấm công mới nhất
    List<Attendance> findAllByOrderByCheckInDesc();

    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
}