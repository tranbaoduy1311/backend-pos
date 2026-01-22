package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.*;
import com.nhahang.pos.pos_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private TableRepository tableRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ProductRecipeRepository recipeRepo;
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private PromotionRepository promotionRepo;

    // 1. Lấy hóa đơn đang chờ
    @GetMapping("/table/{tableId}")
    public Order getOrderByTable(@PathVariable Long tableId) {
        return orderRepo.findPendingOrderByTableId(tableId).orElse(null);
    }

    // 2. Thêm món vào bàn
    @PostMapping("/add")
    public Order addProductToTable(
            @RequestParam Long tableId,
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(required = false, defaultValue = "") String note) {

        Order order = orderRepo.findPendingOrderByTableId(tableId).orElseGet(() -> {
            Order newOrder = new Order();
            newOrder.setTableId(tableId);
            newOrder.setStatus("PENDING");
            newOrder.setTotalPrice(0.0);

            DiningTable table = tableRepo.findById(tableId).orElseThrow();
            table.setStatus("Có khách");
            tableRepo.save(table);

            return orderRepo.save(newOrder);
        });

        Product product = productRepo.findById(productId).orElseThrow();

        OrderDetail detail = new OrderDetail();
        detail.setOrderId(order.getId());
        detail.setProductId(productId);
        detail.setProductName(product.getName());
        detail.setQuantity(quantity);
        detail.setPrice(product.getPrice());
        detail.setNote(note);

        orderDetailRepo.save(detail);

        order.setTotalPrice(order.getTotalPrice() + (product.getPrice() * quantity));
        return orderRepo.save(order);
    }

    // 3. THANH TOÁN (ĐÃ SỬA LỖI TRỪ KHO)
    @PostMapping("/{orderId}/pay")
    public void payOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) Long customerId) {

        Order order = orderRepo.findById(orderId).orElseThrow();

        if ("PAID".equals(order.getStatus()))
            return;

        order.setStatus("PAID");

        // --- A. TÍCH ĐIỂM ---
        if (customerId != null) {
            order.setCustomerId(customerId);
            Customer customer = customerRepo.findById(customerId).orElse(null);
            if (customer != null) {
                int pointsEarned = (int) (order.getTotalPrice() / 10000);
                customer.setPoints(customer.getPoints() + pointsEarned);
                customerRepo.save(customer);
            }
        }

        // --- B. TRỪ KHO TỰ ĐỘNG (ĐÃ SỬA) ---
        List<OrderDetail> details = orderDetailRepo.findByOrderId(orderId);

        for (OrderDetail item : details) {
            // Lấy công thức của món ăn
            List<ProductRecipe> recipes = recipeRepo.findByProductId(item.getProductId());

            for (ProductRecipe recipe : recipes) {
                // --- SỬA LỖI TẠI ĐÂY ---
                // Lấy trực tiếp đối tượng Ingredient từ quan hệ @ManyToOne
                Ingredient ing = recipe.getIngredient();

                if (ing != null) {
                    // Tính lượng cần trừ = Định lượng 1 món * Số lượng khách gọi
                    double amountToDeduct = recipe.getQuantityRequired() * item.getQuantity();

                    // Cập nhật số lượng tồn kho
                    ing.setQuantity(ing.getQuantity() - amountToDeduct);
                    ingredientRepo.save(ing);
                }
            }
        }

        orderRepo.save(order);

        // Trả bàn
        DiningTable table = tableRepo.findById(order.getTableId()).orElseThrow();
        table.setStatus("Trống");
        tableRepo.save(table);
    }

    // 4. Lấy chi tiết
    @GetMapping("/{orderId}/details")
    public List<OrderDetail> getOrderDetails(@PathVariable Long orderId) {
        return orderDetailRepo.findByOrderId(orderId);
    }

    // --- THÊM API XÓA MÓN ---
    @DeleteMapping("/details/{id}")
    public Order deleteOrderDetail(@PathVariable Long id) {
        OrderDetail detail = orderDetailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        Order order = orderRepo.findById(detail.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // 1. Trừ tiền món đó ra khỏi tổng
        double amountToSubtract = detail.getPrice() * detail.getQuantity();
        order.setTotalPrice(order.getTotalPrice() - amountToSubtract);

        // 2. QUAN TRỌNG: Khi xóa món, cần Reset lại mã giảm giá (để tránh lỗi logic giá
        // âm hoặc không đủ điều kiện)
        // Nếu muốn giữ mã thì phải tính lại, nhưng an toàn nhất là bắt nhân viên áp mã
        // lại.
        order.setVoucherCode(null);
        order.setDiscountAmount(0.0);
        order.setFinalPrice(order.getTotalPrice()); // Giá cuối về lại bằng giá gốc

        // 3. Xóa chi tiết và lưu Order
        orderDetailRepo.delete(detail);
        return orderRepo.save(order);
    }

    // API MỚI: Áp dụng mã giảm giá
    @PostMapping("/{orderId}/apply-voucher")
    public Order applyVoucher(@PathVariable Long orderId, @RequestParam String code) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // 1. Tìm khuyến mãi
        Promotion promo = promotionRepo.findByCodeAndStatus(code, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại hoặc đã hết hạn!"));

        // 2. Validate điều kiện
        // Check ngày
        LocalDateTime now = LocalDateTime.now();
        if (promo.getStartDate() != null && now.isBefore(promo.getStartDate()))
            throw new RuntimeException("Mã chưa có hiệu lực");
        if (promo.getEndDate() != null && now.isAfter(promo.getEndDate()))
            throw new RuntimeException("Mã đã hết hạn");

        // Check giờ vàng (Happy Hour)
        if (promo.getStartHour() != null && promo.getEndHour() != null) {
            LocalTime timeNow = LocalTime.now();
            if (timeNow.isBefore(promo.getStartHour()) || timeNow.isAfter(promo.getEndHour())) {
                throw new RuntimeException(
                        "Mã chỉ áp dụng trong khung giờ: " + promo.getStartHour() + " - " + promo.getEndHour());
            }
        }

        // Check giá trị đơn tối thiểu
        if (order.getTotalPrice() < promo.getMinOrderValue()) {
            throw new RuntimeException("Đơn hàng phải từ " + promo.getMinOrderValue() + "đ mới được áp dụng!");
        }

        // 3. Tính toán giảm giá
        double discount = 0;
        if ("FIXED".equals(promo.getDiscountType())) {
            discount = promo.getDiscountValue();
        } else if ("PERCENTAGE".equals(promo.getDiscountType())) {
            discount = order.getTotalPrice() * (promo.getDiscountValue() / 100);
            // Check giảm tối đa
            if (promo.getMaxDiscountAmount() != null && promo.getMaxDiscountAmount() > 0) {
                discount = Math.min(discount, promo.getMaxDiscountAmount());
            }
        }

        // 4. Cập nhật Order
        order.setVoucherCode(code);
        order.setDiscountAmount(discount);
        double finalPrice = order.getTotalPrice() - discount;
        order.setFinalPrice(finalPrice < 0 ? 0 : finalPrice); // Không để âm tiền

        return orderRepo.save(order);
    }

    // API MỚI: Lấy danh sách khuyến mãi đang hoạt động cho POS
    @GetMapping("/promotions/active")
    public List<Promotion> getActivePromotions() {
        List<Promotion> allActive = promotionRepo.findByStatus("ACTIVE");

        // Lọc thêm: Chỉ lấy các mã còn hạn sử dụng (Ngày hiện tại nằm trong khoảng
        // Start-End)
        LocalDateTime now = LocalDateTime.now();
        return allActive.stream()
                .filter(p -> (p.getStartDate() == null || !now.isBefore(p.getStartDate())) &&
                        (p.getEndDate() == null || !now.isAfter(p.getEndDate())))
                .toList();
    }
}