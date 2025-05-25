package com.assignment.tracking.repository;

import com.assignment.tracking.entity.AuditLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends ReactiveCrudRepository<AuditLog, Long> {
}