package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    // Tìm tất cả món ăn của 1 hóa đơn
    List<OrderDetail> findByOrderId(Long orderId);
}