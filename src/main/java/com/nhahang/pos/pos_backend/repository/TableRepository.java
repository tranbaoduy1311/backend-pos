package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<DiningTable, Long> {
}