package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.Order; // Import Entity Order
import com.nhahang.pos.pos_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private TableRepository tableRepo;
    @Autowired
    private OrderRepository orderRepo;

    // 1. API Lấy thống kê tổng quan
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProducts", productRepo.count());
        stats.put("totalTables", tableRepo.count());
        stats.put("totalOrders", orderRepo.count());

        // Tính doanh thu HÔM NAY
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        Double todayRevenue = orderRepo.sumRevenueBetween(startOfToday, now);
        stats.put("revenue", todayRevenue != null ? todayRevenue : 0.0);

        // Biểu đồ 7 ngày
        List<Map<String, Object>> chartData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusSeconds(1);

            Double dailyRev = orderRepo.sumRevenueBetween(start, end);

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("name", date.format(formatter));
            dayStat.put("revenue", dailyRev != null ? dailyRev : 0.0);

            chartData.add(dayStat);
        }
        stats.put("chartData", chartData);

        return stats;
    }

    @GetMapping("/orders/today")
    public List<Order> getTodayOrders() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        return orderRepo.findAllByStatusAndCreatedAtBetween("PAID", start, end);
    }
}