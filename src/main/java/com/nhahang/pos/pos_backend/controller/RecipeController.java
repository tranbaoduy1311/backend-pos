package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.ProductRecipe;
import com.nhahang.pos.pos_backend.repository.ProductRecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "http://localhost:5173")
public class RecipeController {

    @Autowired
    private ProductRecipeRepository recipeRepo;

    // 1. Lấy công thức của 1 món ăn
    @GetMapping("/{productId}")
    public List<ProductRecipe> getRecipe(@PathVariable Long productId) {
        return recipeRepo.findByProductId(productId);
    }

    // 2. Lưu công thức
    @PostMapping("/{productId}")
    @Transactional
    public ResponseEntity<?> saveRecipe(@PathVariable Long productId, @RequestBody List<ProductRecipe> ingredients) {
        try {
            // Bước 1: Xóa công thức cũ
            recipeRepo.deleteByProductId(productId);

            // Bước 2: Gán ID món ăn cho từng nguyên liệu mới
            for (ProductRecipe item : ingredients) {
                item.setProductId(productId);
            }

            // Bước 3: Lưu danh sách mới
            List<ProductRecipe> savedData = recipeRepo.saveAll(ingredients);

            return ResponseEntity.ok(savedData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi khi lưu công thức: " + e.getMessage());
        }
    }
}