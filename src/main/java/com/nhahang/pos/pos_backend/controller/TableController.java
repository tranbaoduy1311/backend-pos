package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.DiningTable;
import com.nhahang.pos.pos_backend.entity.Reservation;
import com.nhahang.pos.pos_backend.repository.ReservationRepository;
import com.nhahang.pos.pos_backend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    // Lấy danh sách bàn
    @GetMapping
    public List<DiningTable> getAllTables() {
        return tableRepository.findAll();
    }

    // Thêm bàn mới
    @PostMapping
    public DiningTable createTable(@RequestBody DiningTable table) {
        // Mặc định khi tạo bàn mới thì status là "Trống"
        if (table.getStatus() == null)
            table.setStatus("Trống");
        return tableRepository.save(table);
    }

    // Cập nhật trạng thái bàn (Ví dụ: Khách vào ngồi -> Đổi thành "Có khách")
    @PutMapping("/{id}/status")
    public DiningTable updateStatus(@PathVariable Long id, @RequestBody String status) {
        DiningTable table = tableRepository.findById(id).orElseThrow();
        table.setStatus(status);
        return tableRepository.save(table);
    }

    // Xóa bàn
    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable Long id) {
        tableRepository.deleteById(id);
    }

    @Autowired
    private ReservationRepository reservationRepo;

    // API: Gán bàn cho khách đặt trước
    @PostMapping("/assign-reservation")
    public void assignReservation(@RequestParam Long tableId, @RequestParam Long reservationId) {
        DiningTable table = tableRepository.findById(tableId).orElseThrow();
        Reservation res = reservationRepo.findById(reservationId).orElseThrow();

        // 1. Cập nhật trạng thái bàn
        table.setStatus("Đã đặt");

        // 2. Lưu thông tin khách vào bàn để hiển thị cho nhân viên
        table.setGuestInfo(res.getCustomerName() + " - " + res.getPhone());

        tableRepository.save(table);

        // 3. Cập nhật đơn đặt bàn
        res.setStatus("CONFIRMED");
        reservationRepo.save(res);
    }

    @PostMapping("/{id}/free")
    public void freeTable(@PathVariable Long id) {
        DiningTable table = tableRepository.findById(id).orElseThrow();
        table.setStatus("Trống");
        table.setGuestInfo(null);
        tableRepository.save(table);
    }
}