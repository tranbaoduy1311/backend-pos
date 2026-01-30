package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Product;
import com.nhahang.pos.pos_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        // Mặc định khi thêm mới là đang kinh doanh
        if (product.getStatus() == null)
            product.setStatus(true);
        return productRepository.save(product);
    }

    // BỔ SUNG THÊM API XÓA ĐỂ KHỚP VỚI FRONTEND
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}