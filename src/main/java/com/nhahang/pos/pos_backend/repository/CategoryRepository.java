package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}