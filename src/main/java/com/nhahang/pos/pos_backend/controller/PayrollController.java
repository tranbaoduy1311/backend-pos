package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.*;
import com.nhahang.pos.pos_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payrolls")
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {

    @Autowired
    private PayrollRepository payrollRepo;
    @Autowired
    private EmployeeRepository employeeRepo;
    @Autowired
    private AttendanceRepository attendanceRepo;

    // 1. Lấy bảng lương theo tháng
    @GetMapping
    public List<Payroll> getPayrolls(@RequestParam int month, @RequestParam int year) {
        return payrollRepo.findByMonthAndYear(month, year);
    }

    // 2. TÍNH LƯƠNG (LOGIC Ở ĐÂY)
    @PostMapping("/calculate")
    @Transactional
    public List<Payroll> calculatePayroll(@RequestParam int month, @RequestParam int year) {
        // Xóa dữ liệu cũ của tháng đó để tính lại
        payrollRepo.deleteByMonthAndYear(month, year);

        List<Employee> employees = employeeRepo.findAll();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // --- CẤU HÌNH CỐ ĐỊNH ---
        int STANDARD_DAYS = 26; // Công chuẩn
        int ALLOWED_OFF_DAYS = 4; // Được nghỉ tối đa 4 ngày
        double PENALTY_PER_DAY = 500000; // Phạt 500k/ngày nếu nghỉ lố
        double OVERTIME_RATE = 35000; // Tăng ca 35k/giờ

        for (Employee emp : employees) {
            // Lấy dữ liệu chấm công
            List<Attendance> attendances = attendanceRepo.findByEmployeeIdAndWorkDateBetween(
                    emp.getId(), startDate, endDate);

            Payroll payroll = new Payroll();
            payroll.setEmployeeId(emp.getId());
            payroll.setEmployeeName(emp.getName());
            payroll.setMonth(month);
            payroll.setYear(year);
            payroll.setStatus("DRAFT");
            payroll.setStandardWorkDays(STANDARD_DAYS);

            // 1. Tổng hợp số liệu
            int actualWorkDays = attendances.size();

            // Tổng giờ làm (cho Part-time)
            double totalWorkHours = attendances.stream().mapToDouble(Attendance::getWorkHours).sum();

            // Tổng giờ tăng ca
            double overtimeHours = attendances.stream().mapToDouble(Attendance::getOvertimeHours).sum();

            double totalSalary = 0;
            double deduction = 0;
            double bonus = 0;

            if ("HOURLY".equals(emp.getSalaryType())) {
                // A. PART-TIME (Lương theo giờ)
                double rate = emp.getHourlyRate() != null ? emp.getHourlyRate() : 20000;
                payroll.setSalaryType("HOURLY");
                payroll.setBaseSalary(rate);

                // Logic Part-time: (Giờ làm * Lương giờ) + (Tăng ca * 35k)
                double normalHours = totalWorkHours - overtimeHours;
                if (normalHours < 0)
                    normalHours = 0;

                // Tiền giờ thường
                double regularPay = normalHours * rate;

                // Tiền tăng ca (Theo yêu cầu là 35k/h)
                bonus = overtimeHours * OVERTIME_RATE;

                totalSalary = regularPay + bonus;
                deduction = 0;

            } else {
                // B. FULL-TIME (Lương tháng - Logic của bạn)
                double monthlySalary = emp.getSalary() != null ? emp.getSalary() : 5000000;
                payroll.setSalaryType("MONTHLY");
                payroll.setBaseSalary(monthlySalary);

                // 1. Tính số ngày nghỉ
                int daysOff = STANDARD_DAYS - actualWorkDays;
                if (daysOff < 0)
                    daysOff = 0;

                // 2. Tính phạt (Nếu nghỉ quá 4 ngày)
                int penaltyDays = 0;
                if (daysOff > ALLOWED_OFF_DAYS) {
                    penaltyDays = daysOff - ALLOWED_OFF_DAYS;
                }

                // Trừ 500k cho mỗi ngày nghỉ lố (không phép)
                deduction = penaltyDays * PENALTY_PER_DAY;

                // 3. Tính tiền tăng ca (35k/giờ)
                bonus = overtimeHours * OVERTIME_RATE;

                // 4. Tổng thực nhận
                // Công thức: Lương cứng - Phạt + Tăng ca
                totalSalary = monthlySalary - deduction + bonus;
            }

            // Lưu dữ liệu
            payroll.setTotalWorkDays(actualWorkDays);
            payroll.setTotalWorkHours(Math.round(totalWorkHours * 100.0) / 100.0);
            payroll.setOvertimeHours(overtimeHours);

            payroll.setDeduction(deduction);
            payroll.setBonus(bonus);

            // Đảm bảo lương không bị âm
            if (totalSalary < 0)
                totalSalary = 0;
            payroll.setTotalSalary(Math.round(totalSalary * 100.0) / 100.0);

            payrollRepo.save(payroll);
        }

        return payrollRepo.findByMonthAndYear(month, year);
    }

    // 3. Chốt lương
    @PostMapping("/{id}/pay")
    public Payroll paySalary(@PathVariable Long id) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        p.setStatus("PAID");
        return payrollRepo.save(p);
    }
}