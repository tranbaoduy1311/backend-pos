package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone); // Tìm khách theo SĐT
}