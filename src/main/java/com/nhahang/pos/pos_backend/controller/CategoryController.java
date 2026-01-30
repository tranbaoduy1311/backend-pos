package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Category;
import com.nhahang.pos.pos_backend.repository.CategoryRepository;
import com.nhahang.pos.pos_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. Lấy danh sách danh mục kèm số lượng món ăn (Đã gộp logic)
    @GetMapping
    public List<Map<String, Object>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(cat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cat.getId());
            map.put("name", cat.getName());
            map.put("productCount", productRepository.countByCategoryId(cat.getId()));
            return map;
        }).collect(Collectors.toList());
    }

    // 2. Thêm hoặc cập nhật danh mục mới
    @PostMapping
    public Category saveCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    // 3. Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        // Kiểm tra xem danh mục có chứa món ăn không
        if (productRepository.existsByCategoryId(id)) {
            return ResponseEntity.badRequest().body(
                    "Lỗi: Không thể xóa danh mục đang chứa món ăn! Hãy xóa hoặc chuyển món ăn sang danh mục khác trước.");
        }

        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}