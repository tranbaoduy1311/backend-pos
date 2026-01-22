package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Supplier;
import com.nhahang.pos.pos_backend.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/suppliers")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepo;

    // 1. Lấy danh sách
    @GetMapping
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    // 2. Thêm mới
    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) {
        if (supplier.getTotalDebt() == null)
            supplier.setTotalDebt(0.0);
        return supplierRepo.save(supplier);
    }

    // 3. Cập nhật thông tin
    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier req) {
        return supplierRepo.findById(id).map(s -> {
            s.setName(req.getName());
            s.setPhone(req.getPhone());
            s.setAddress(req.getAddress());
            return supplierRepo.save(s);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy NCC"));
    }

    // 4. Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        supplierRepo.deleteById(id);
    }

    // 5. THANH TOÁN CÔNG NỢ (Trả tiền cho NCC)
    @PostMapping("/{id}/pay-debt")
    public Supplier payDebt(@PathVariable Long id, @RequestParam Double amount) {
        Supplier s = supplierRepo.findById(id).orElseThrow();
        // Trừ nợ
        double newDebt = s.getTotalDebt() - amount;
        s.setTotalDebt(newDebt < 0 ? 0 : newDebt); // Không để nợ âm
        return supplierRepo.save(s);
    }
}