package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Promotion;
import com.nhahang.pos.pos_backend.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminPromotionController {

    @Autowired
    private PromotionRepository promotionRepo;

    // 1. Lấy danh sách tất cả
    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionRepo.findAll();
    }

    // 2. Tạo mới
    @PostMapping
    public Promotion createPromotion(@RequestBody Promotion promotion) {
        // Có thể thêm validate trùng mã code ở đây nếu cần
        if (promotion.getStatus() == null)
            promotion.setStatus("ACTIVE");
        return promotionRepo.save(promotion);
    }

    // 3. Cập nhật
    @PutMapping("/{id}")
    public Promotion updatePromotion(@PathVariable Long id, @RequestBody Promotion updatedPromo) {
        return promotionRepo.findById(id).map(promo -> {
            promo.setCode(updatedPromo.getCode());
            promo.setDescription(updatedPromo.getDescription());
            promo.setDiscountType(updatedPromo.getDiscountType());
            promo.setDiscountValue(updatedPromo.getDiscountValue());
            promo.setMinOrderValue(updatedPromo.getMinOrderValue());
            promo.setMaxDiscountAmount(updatedPromo.getMaxDiscountAmount());
            promo.setStartDate(updatedPromo.getStartDate());
            promo.setEndDate(updatedPromo.getEndDate());
            promo.setStartHour(updatedPromo.getStartHour());
            promo.setEndHour(updatedPromo.getEndHour());
            promo.setStatus(updatedPromo.getStatus());
            return promotionRepo.save(promo);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi!"));
    }

    // 4. Xóa
    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable Long id) {
        promotionRepo.deleteById(id);
    }
}