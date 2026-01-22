package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.ImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
    // Tìm lịch sử nhập theo ngày (để hiển thị "Trong ngày")
    List<ImportHistory> findByImportDate(LocalDate date);
}