package com.aaharrakshak.audit;

import com.aaharrakshak.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(User actor, String action, String entityType, String entityId, String details) {
        auditLogRepository.save(new AuditLog(actor, action, entityType, entityId, details));
    }
}
