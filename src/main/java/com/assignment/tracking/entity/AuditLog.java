package com.assignment.tracking.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("audit_log")
public class AuditLog {
    @Id
    private String id;

    private String trackingNumber;
    private String originCountryId;
    private String destinationCountryId;
    private String customerId;
    private String customerSlug;
    private String customerName;
    private Instant createdAt;
}