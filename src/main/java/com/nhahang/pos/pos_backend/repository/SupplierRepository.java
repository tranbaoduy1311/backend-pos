package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}