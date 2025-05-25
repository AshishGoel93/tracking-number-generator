package com.assignment.tracking.controller;

import com.assignment.tracking.dto.*;
import com.assignment.tracking.service.TrackingNumberService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrackingNumberController {

    private final TrackingNumberService trackingNumberService;

    @GetMapping("/next-tracking-number")
    public Mono<TrackingResponse> getNextTrackingNumber(
            @RequestParam("origin_country_id")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country code must be 2 uppercase letters")
            String originCountryId,

            @RequestParam("destination_country_id")
            @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country code must be 2 uppercase letters")
            String destinationCountryId,

            @RequestParam("weight")
            @Digits(integer = 10, fraction = 3)
            @NotNull
            BigDecimal weight,

            @RequestParam("created_at")
            @NotBlank(message = "Created at timestamp is required")
            String createdAt,

            @RequestParam("customer_id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
                    message = "Invalid UUID format for customer_id")
            String customerId,

            @RequestParam("customer_name")
            @NotBlank(message = "Customer name is required")
            String customerName,

            @RequestParam("customer_slug")
            @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Customer slug must be in kebab-case")
            String customerSlug
    ) {
        TrackingRequest request = new TrackingRequest();
        request.setOriginCountryId(originCountryId);
        request.setDestinationCountryId(destinationCountryId);
        request.setCustomerId(customerId);
        request.setCustomerSlug(customerSlug);
        request.setCustomerName(customerName);
        request.setWeight(weight);
        request.setTimestamp(createdAt);

        log.info("Request received for generating the next tracking number: {}", request);
        return trackingNumberService.generateTrackingNumber(request);
    }
}