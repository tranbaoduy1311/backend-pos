package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Customer;
import com.nhahang.pos.pos_backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {
    @Autowired
    private CustomerRepository customerRepo;

    // Tìm khách theo SĐT TICHDIEM
    @GetMapping("/search")
    public Customer getCustomerByPhone(@RequestParam String phone) {
        return customerRepo.findByPhone(phone).orElse(null);
    }

    // Tạo khách mới
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepo.save(customer);
    }
}