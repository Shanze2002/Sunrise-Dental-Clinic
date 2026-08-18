package com.sunrisedental.service;

import com.sunrisedental.dao.AuditLogDAO;
import com.sunrisedental.model.AuditLog;

import java.util.List;

/**
 * Service: AuditService
 */
public class AuditService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public void log(Integer userId, String action, String entityName, String entityId, String details, String ipAddress) {
        auditLogDAO.log(userId, action, entityName, entityId, details, ipAddress);
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogDAO.findRecent(limit);
    }
}
