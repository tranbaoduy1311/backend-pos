package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.*;
import com.nhahang.pos.pos_backend.repository.*;
import com.nhahang.pos.pos_backend.service.AuditService; // 1. Import Service vào
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminFeatureController {

    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private ImportHistoryRepository importHistoryRepo;
    @Autowired
    private SupplierRepository supplierRepo;

    @Autowired
    private AuditService auditService; // 2. Tiêm AuditService vào để sử dụng

    // ==========================================
    // 1. QUẢN LÝ NHÂN VIÊN (EMPLOYEES)
    // ==========================================

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return employeeRepo.findAll().stream()
                .filter(emp -> emp.getStatus() == null || emp.getStatus() == 1)
                .collect(Collectors.toList());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> saveEmployee(@RequestBody Employee emp) {
        if ((emp.getSalary() != null && emp.getSalary() < 0) ||
                (emp.getHourlyRate() != null && emp.getHourlyRate() < 0)) {
            return ResponseEntity.badRequest().body("Lỗi: Lương không được là số âm!");
        }

        Optional<Employee> existingEmp = employeeRepo.findByEmployeeCode(emp.getEmployeeCode());
        if (existingEmp.isPresent()) {
            if (emp.getId() == null || !existingEmp.get().getId().equals(emp.getId())) {
                return ResponseEntity.badRequest().body("Lỗi: Mã nhân viên " + emp.getEmployeeCode() + " đã tồn tại!");
            }
        }

        if (emp.getStartDate() == null)
            emp.setStartDate(java.time.LocalDate.now());
        if (emp.getStatus() == null)
            emp.setStatus(1);

        Employee saved = employeeRepo.save(emp);

        // 3. GHI LOG: Thêm nhân viên mới
        auditService.log("ADD_EMPLOYEE",
                "Thêm mới nhân viên: " + saved.getName() + " (" + saved.getEmployeeCode() + ")", "Admin");

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee empDetails) {
        Employee emp = employeeRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        emp.setName(empDetails.getName());
        emp.setEmployeeCode(empDetails.getEmployeeCode());
        emp.setRole(empDetails.getRole());
        emp.setPhone(empDetails.getPhone());
        emp.setSalary(empDetails.getSalary());
        emp.setSalaryType(empDetails.getSalaryType());
        emp.setHourlyRate(empDetails.getHourlyRate());

        Employee updated = employeeRepo.save(emp);

        // 4. GHI LOG: Cập nhật nhân viên
        auditService.log("UPDATE_EMPLOYEE", "Cập nhật thông tin nhân viên: " + updated.getName(), "Admin");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        return employeeRepo.findById(id).map(emp -> {
            if ("ADMIN".equalsIgnoreCase(emp.getRole())) {
                return ResponseEntity.badRequest().body("Lỗi bảo mật: Không thể xóa tài khoản Quản trị viên!");
            }

            try {
                emp.setStatus(0);
                employeeRepo.save(emp);

                // 5. GHI LOG: Xóa nhân viên
                auditService.log("DELETE_EMPLOYEE",
                        "Đã xóa nhân viên: " + emp.getName() + " (Mã: " + emp.getEmployeeCode() + ")", "Admin");

                return ResponseEntity.ok().body("Đã xóa nhân viên thành công!");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 2. QUẢN LÝ KHO (INGREDIENTS)
    // ==========================================

    @PostMapping("/ingredients")
    public Ingredient saveIngredient(@RequestBody Ingredient ing) {
        Ingredient saved = ingredientRepo.save(ing);
        // 6. GHI LOG: Tạo nguyên liệu mới
        auditService.log("CREATE_INGREDIENT", "Tạo mới nguyên liệu: " + saved.getName(), "Admin");
        return saved;
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

        // 7. GHI LOG: Nhập kho nguyên liệu
        auditService.log("IMPORT_INGREDIENT",
                "Nhập kho " + quantity + " " + ingredient.getUnit() + " " + ingredient.getName(), "Admin");

        if (Boolean.FALSE.equals(isPaid) && supplierId != null) {
            Supplier supplier = supplierRepo.findById(supplierId).orElseThrow();
            double currentDebt = supplier.getTotalDebt() != null ? supplier.getTotalDebt() : 0.0;
            supplier.setTotalDebt(currentDebt + (quantity * price));
            supplierRepo.save(supplier);
        }
        return ingredient;
    }

    // Các hàm khác giữ nguyên...

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