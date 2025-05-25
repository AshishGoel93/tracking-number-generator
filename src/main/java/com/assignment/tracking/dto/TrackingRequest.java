package com.assignment.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {
    private String originCountryId;
    private String destinationCountryId;
    private String customerSlug;
    private String customerName;
    private String customerId;
    private BigDecimal weight;
    private String timestamp;
}
