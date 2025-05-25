package com.assignment.tracking.service;

import com.assignment.tracking.dto.TrackingRequest;
import com.assignment.tracking.dto.TrackingResponse;
import com.assignment.tracking.entity.AuditLog;
import com.assignment.tracking.exception.TrackingServiceException;
import com.assignment.tracking.repository.AuditLogRepository;
import com.assignment.tracking.util.TrackingNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingNumberService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final TrackingNumberGenerator generator;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final AuditLogRepository auditLogRepository;

    public Mono<TrackingResponse> generateTrackingNumber(TrackingRequest request) {
        String origin = sanitizeCode(request.getOriginCountryId());
        String destination = sanitizeCode(request.getDestinationCountryId());

        return extractDate(request.getTimestamp())
                .map(date -> buildRedisKey(origin, destination, date))
                .flatMap(redisKey -> redisTemplate.opsForValue().increment(redisKey)
                        .switchIfEmpty(Mono.error(new TrackingServiceException("Failed to increment Redis counter")))
                        .map(counter -> generator.generate(origin, destination, extractDateString(request.getTimestamp()), counter))
                        .flatMap(trackingNumber -> saveAuditLog(trackingNumber, request)
                                .thenReturn(new TrackingResponse(trackingNumber, OffsetDateTime.now(ZoneOffset.UTC).toString())))
                );
    }

    private Mono<String> extractDate(String timestamp) {
        try {
            OffsetDateTime odt = OffsetDateTime.parse(timestamp);
            LocalDate date = odt.toLocalDate();
            return Mono.just(date.format(DATE_FORMATTER));
        } catch (Exception e) {
            return Mono.error(new TrackingServiceException("Invalid timestamp format", e));
        }
    }

    private String extractDateString(String timestamp) {
        OffsetDateTime odt = OffsetDateTime.parse(timestamp);
        return odt.toLocalDate().format(DATE_FORMATTER);
    }

    private String buildRedisKey(String origin, String destination, String date) {
        return String.join(":", "counter", origin, destination, date);
    }

    private Mono<Void> saveAuditLog(String trackingNumber, TrackingRequest request) {
        AuditLog logEntry = new AuditLog(
                UUID.randomUUID().toString(),
                trackingNumber,
                sanitizeCode(request.getOriginCountryId()),
                sanitizeCode(request.getDestinationCountryId()),
                request.getCustomerId(),
                request.getCustomerSlug(),
                request.getCustomerName(),
                OffsetDateTime.parse(request.getTimestamp()).toInstant()
        );

        return auditLogRepository.save(logEntry)
                .doOnSuccess(saved -> log.info("Saved audit log for tracking number: {}", trackingNumber))
                .doOnError(err -> log.error("Failed to save audit log", err))
                .onErrorResume(err -> Mono.empty())
                .then();
    }

    private String sanitizeCode(String input) {
        if (input == null) {
            return "XX";
        }
        String sanitized = input.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        return sanitized.substring(0, Math.min(2, sanitized.length()));
    }
}
