package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.ProductRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRecipeRepository extends JpaRepository<ProductRecipe, Long> {

    // Tìm danh sách nguyên liệu theo món ăn
    List<ProductRecipe> findByProductId(Long productId);

    // --- BẠN ĐANG THIẾU DÒNG NÀY ---
    // Hàm xóa tất cả nguyên liệu của 1 món ăn
    void deleteByProductId(Long productId);
}