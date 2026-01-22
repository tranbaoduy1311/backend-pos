package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCodeAndStatus(String code, String status);

    List<Promotion> findByStatus(String status);
}