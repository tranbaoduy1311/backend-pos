package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Reservation;
import com.nhahang.pos.pos_backend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {
    @Autowired
    private ReservationRepository reservationRepo;

    @GetMapping
    public List<Reservation> getAll() {
        return reservationRepo.findAll();
    }

    // API xác nhận khách đã đến (Xóa hoặc đổi trạng thái)
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationRepo.deleteById(id);
    }

    // API cho khách đặt bàn
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        reservation.setStatus("PENDING"); // Mặc định là chờ xác nhận
        // Nếu chưa có thời gian, lấy giờ hiện tại (hoặc xử lý ở Frontend gửi lên)
        if (reservation.getReservationTime() == null) {
            reservation.setReservationTime(java.time.LocalDateTime.now());
        }
        return reservationRepo.save(reservation);
    }

}