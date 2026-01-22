package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.*;
import com.nhahang.pos.pos_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép React truy cập
public class AdminFeatureController {

    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private ImportHistoryRepository importHistoryRepo;
    @Autowired
    private SupplierRepository supplierRepo;

    // ==========================================
    // 1. QUẢN LÝ NHÂN VIÊN (EMPLOYEES)
    // ==========================================

    // HÀM NÀY BỊ THIẾU LÚC NÃY - TÔI ĐÃ THÊM LẠI
    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeRepo.findAll();
    }

    @PostMapping("/employees")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee emp) {
        // Chặn lương âm
        if ((emp.getSalary() != null && emp.getSalary() < 0) ||
                (emp.getHourlyRate() != null && emp.getHourlyRate() < 0)) {
            return ResponseEntity.badRequest().body("Lỗi: Lương không được là số âm!");
        }

        // Kiểm tra trùng Mã nhân viên
        Optional<Employee> existingEmp = employeeRepo.findByEmployeeCode(emp.getEmployeeCode());
        if (existingEmp.isPresent()) {
            if (emp.getId() == null || !existingEmp.get().getId().equals(emp.getId())) {
                return ResponseEntity.badRequest().body("Lỗi: Mã nhân viên " + emp.getEmployeeCode() + " đã tồn tại!");
            }
        }

        if (emp.getStartDate() == null)
            emp.setStartDate(java.time.LocalDate.now());

        return ResponseEntity.ok(employeeRepo.save(emp));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee empDetails) {
        Employee emp = employeeRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        if ((empDetails.getSalary() != null && empDetails.getSalary() < 0) ||
                (empDetails.getHourlyRate() != null && empDetails.getHourlyRate() < 0)) {
            return ResponseEntity.badRequest().body("Lỗi: Lương không được là số âm!");
        }

        emp.setName(empDetails.getName());
        emp.setEmployeeCode(empDetails.getEmployeeCode());
        emp.setRole(empDetails.getRole());
        emp.setPhone(empDetails.getPhone());
        emp.setSalary(empDetails.getSalary());
        emp.setSalaryType(empDetails.getSalaryType());
        emp.setHourlyRate(empDetails.getHourlyRate());

        return ResponseEntity.ok(employeeRepo.save(emp));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            if (!employeeRepo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            employeeRepo.deleteById(id);
            return ResponseEntity.ok().body("Xóa thành công!");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body("Không thể xóa: Nhân viên này đã có dữ liệu chấm công hoặc hóa đơn!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. QUẢN LÝ KHO (INGREDIENTS)
    // ==========================================

    @GetMapping("/ingredients")
    public List<Ingredient> getIngredients() {
        return ingredientRepo.findAll();
    }

    @PostMapping("/ingredients")
    public Ingredient saveIngredient(@RequestBody Ingredient ing) {
        return ingredientRepo.save(ing);
    }

    @GetMapping("/ingredients/history")
    public List<ImportHistory> getAllHistory() {
        return importHistoryRepo.findAll();
    }

    @PostMapping("/ingredients/import")
    @Transactional
    public Ingredient importIngredient(
            @RequestParam Long id,
            @RequestParam Double quantity,
            @RequestParam Double price,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "true") Boolean isPaid) {
        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));

        ingredient.setQuantity(ingredient.getQuantity() + quantity);
        ingredient.setCostPrice(price);
        ingredientRepo.save(ingredient);

        ImportHistory history = new ImportHistory();
        history.setIngredientName(ingredient.getName());
        history.setUnit(ingredient.getUnit());
        history.setQuantity(quantity);
        history.setTotalCost(quantity * price);
        history.setSupplierId(supplierId);
        history.setIsPaidDebt(isPaid);
        importHistoryRepo.save(history);

        if (Boolean.FALSE.equals(isPaid) && supplierId != null) {
            Supplier supplier = supplierRepo.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Nhà cung cấp"));
            double currentDebt = supplier.getTotalDebt() != null ? supplier.getTotalDebt() : 0.0;
            supplier.setTotalDebt(currentDebt + (quantity * price));
            supplierRepo.save(supplier);
        }
        return ingredient;
    }

    @PutMapping("/ingredients/{id}")
    public Ingredient updateIngredient(@PathVariable Long id, @RequestParam(required = false) Double quantity,
            @RequestBody(required = false) Ingredient updatedIng) {
        Ingredient ing = ingredientRepo.findById(id).orElseThrow();
        if (quantity != null) {
            ing.setQuantity(quantity);
            return ingredientRepo.save(ing);
        }
        if (updatedIng != null) {
            ing.setName(updatedIng.getName());
            ing.setUnit(updatedIng.getUnit());
            ing.setCostPrice(updatedIng.getCostPrice());
            if (updatedIng.getQuantity() != null)
                ing.setQuantity(updatedIng.getQuantity());
        }
        return ingredientRepo.save(ing);
    }
}