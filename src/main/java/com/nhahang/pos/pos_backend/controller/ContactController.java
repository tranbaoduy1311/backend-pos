package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.ContactMessage;
import com.nhahang.pos.pos_backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // 1. Khách hàng gửi tin nhắn
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody ContactMessage msg) {
        contactRepository.save(msg);
        return ResponseEntity.ok("Gửi thành công!");
    }

    // 2. Admin lấy danh sách tin nhắn
    @GetMapping
    public List<ContactMessage> getAllMessages() {
        return contactRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // 3. Admin xóa tin nhắn
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        contactRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}