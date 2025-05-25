package com.assignment.tracking.service;

import com.assignment.tracking.dto.TrackingRequest;
import com.assignment.tracking.dto.TrackingResponse;
import com.assignment.tracking.entity.AuditLog;
import com.assignment.tracking.exception.TrackingServiceException;
import com.assignment.tracking.repository.AuditLogRepository;
import com.assignment.tracking.util.TrackingNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingNumberServiceTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    private TrackingNumberService service;

    private final TrackingNumberGenerator generator = new TrackingNumberGenerator();

    @BeforeEach
    void setUp() {
        service = new TrackingNumberService(generator, redisTemplate, auditLogRepository);
    }

    @Test
    void testGenerateTrackingNumber_success() {
        TrackingRequest request = new TrackingRequest(
                "US", "IN", "redbox-logistics", "Redbox Logistics", UUID.randomUUID().toString(),
                new BigDecimal("1.567"), "2018-11-20T19:29:32+08:00");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(auditLogRepository.save(any())).thenReturn(Mono.just(mock(AuditLog.class)));

        Mono<TrackingResponse> result = service.generateTrackingNumber(request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertNotNull(response.getTrackingNumber());
                    assertTrue(response.getTrackingNumber().startsWith("USIN20181120"));
                })
                .verifyComplete();
    }


    @Test
    void testGenerateTrackingNumber_invalidTimestamp() {
        TrackingRequest request = new TrackingRequest
                ("US", "IN", "redbox-logistics", "Redbox Logistics", UUID.randomUUID().toString(),
                        new BigDecimal("1.567"), "invalid-timestamp");

        Mono<TrackingResponse> result = service.generateTrackingNumber(request);

        StepVerifier.create(result)
                .expectError(TrackingServiceException.class)
                .verify();
    }

    @Test
    void testGenerateTrackingNumber_redisFailure() {
        TrackingRequest request = new TrackingRequest
                ("US", "IN", "redbox-logistics", "Redbox Logistics", UUID.randomUUID().toString(),
                        new BigDecimal("1.567"), "2018-11-20T19:29:32+08:00");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(Mono.empty());

        Mono<TrackingResponse> result = service.generateTrackingNumber(request);

        StepVerifier.create(result)
                .expectError(TrackingServiceException.class)
                .verify();
    }

    @Test
    void testAuditLogFailsButTrackingStillReturns() {
        TrackingRequest request = new TrackingRequest
                ("US", "IN", "redbox-logistics", "Redbox Logistics", UUID.randomUUID().toString(),
                        new BigDecimal("1.567"), "2018-11-20T19:29:32+08:00");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(999L));
        when(auditLogRepository.save(any())).thenReturn(Mono.error(new RuntimeException("DB down")));

        Mono<TrackingResponse> result = service.generateTrackingNumber(request);

        StepVerifier.create(result)
                .assertNext(res -> assertTrue(res.getTrackingNumber().startsWith("USIN20181120")))
                .verifyComplete();
    }
}
