package com.nhahang.pos.pos_backend.repository;

import com.nhahang.pos.pos_backend.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
}