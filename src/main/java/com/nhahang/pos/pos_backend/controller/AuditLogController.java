package com.nhahang.pos.pos_backend.controller;

import com.nhahang.pos.pos_backend.entity.AuditLog;
import com.nhahang.pos.pos_backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepo;

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogRepo.findAllByOrderByTimestampDesc();
    }
}