package com.assignment.tracking.controller;

import com.assignment.tracking.dto.TrackingResponse;
import com.assignment.tracking.service.TrackingNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@WebFluxTest(controllers = TrackingNumberController.class)
@AutoConfigureWebTestClient
@Import(TrackingNumberControllerTest.MockConfig.class)
class TrackingNumberControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/next-tracking-number";
    private static final String VALID_CUSTOMER_ID = UUID.randomUUID().toString();

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TrackingNumberService trackingNumberService() {
            TrackingNumberService service = mock(TrackingNumberService.class);
            when(service.generateTrackingNumber(any())).thenReturn(
                    Mono.just(new TrackingResponse("USIN202505250001", "2025-05-25T10:00:00Z"))
            );
            return service;
        }
    }

    @Test
    void testValidRequest_shouldReturnTrackingNumber() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.trackingNumber").isEqualTo("USIN202505250001")
                .jsonPath("$.createdAt").isEqualTo("2025-05-25T10:00:00Z");
    }

    @Test
    void testInvalidOriginCountryId_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "USA")  // Invalid: more than 2 chars
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidDestinationCountryId_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "123")  // Invalid
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidWeight_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "abc")  // Invalid weight
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidTimestamp_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "")  // Empty timestamp
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidCustomerId_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", "invalid-uuid")  // Invalid UUID
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "redbox-logistics")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidCustomerSlug_shouldFail() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("origin_country_id", "US")
                        .queryParam("destination_country_id", "IN")
                        .queryParam("weight", "1.234")
                        .queryParam("created_at", "2025-05-25T10:00:00Z")
                        .queryParam("customer_id", VALID_CUSTOMER_ID)
                        .queryParam("customer_name", "RedBox Logistics")
                        .queryParam("customer_slug", "RedBoxLogistics") // Not kebab-case
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}
