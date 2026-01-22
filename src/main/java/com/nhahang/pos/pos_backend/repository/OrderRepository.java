package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// --- CÁC DÒNG IMPORT QUAN TRỌNG (BẠN ĐANG THIẾU) ---
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Tính tổng doanh thu
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID'")
    Double sumTotalRevenue();

    // Tìm đơn hàng đang chờ của một bàn
    @Query("SELECT o FROM Order o WHERE o.tableId = :tableId AND o.status = 'PENDING'")
    Optional<Order> findPendingOrderByTableId(Long tableId);

    // Tính tổng tiền theo khoảng thời gian (Dùng cho biểu đồ)
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID' AND o.createdAt BETWEEN :start AND :end")
    Double sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Lấy danh sách chi tiết đơn hàng theo thời gian (Dùng cho Popup chi tiết)
    List<Order> findAllByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
}