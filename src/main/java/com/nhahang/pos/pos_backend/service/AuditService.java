package com.nhahang.pos.pos_backend.service;

import com.nhahang.pos.pos_backend.entity.AuditLog;
import com.nhahang.pos.pos_backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepo;

    public void log(String action, String description, String actor) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDescription(description);
        log.setActor(actor);
        auditLogRepo.save(log);
    }
}