package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Bạn có thể thêm hàm tìm nguyên liệu sắp hết hàng (Ví dụ: Số lượng < 5)
    // List<Ingredient> findByQuantityLessThan(Double quantity);
}