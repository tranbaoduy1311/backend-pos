package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Kiểm tra xem có món nào thuộc categoryId này không
    boolean existsByCategoryId(Long categoryId);

    // Đếm số lượng món ăn thuộc categoryId
    long countByCategoryId(Long categoryId);
}