package com.assignment.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {
    @NotBlank
    private String originCountryId;

    @NotBlank
    private String destinationCountryId;

    @NotBlank
    private String customerSlug;

    @NotBlank
    private String customerName;

    private String customerId;
    private BigDecimal weight;
    private String timestamp;
}
