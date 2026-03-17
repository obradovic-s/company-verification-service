package com.example.companyverification.integration;

import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationResponse;
import com.example.companyverification.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "app.failure-rate.free=0",
                "app.failure-rate.premium=0"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VerificationFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VerificationRepository verificationRepository;

    @BeforeEach
    void setup() {
        verificationRepository.deleteAll();
    }

    @Test
    void storesAndRetrievesVerification() {
        UUID id = UUID.randomUUID();
        BackendServiceResponse verifyResponse = restTemplate.getForObject(
                "/backend-service?verificationId={id}&query={query}",
                BackendServiceResponse.class,
                id,
                "CJQ"
        );

        VerificationResponse stored = restTemplate.getForObject(
                "/verifications/{id}",
                VerificationResponse.class,
                id
        );

        assertThat(verifyResponse).isNotNull();
        assertThat(stored).isNotNull();
        assertThat(stored.verificationId()).isEqualTo(id);
        assertThat(stored.status()).isEqualTo(verifyResponse.status());
        assertThat(stored.result()).isEqualTo(verifyResponse.result());
    }

    @Test
    void repeatedVerificationIdReturnsStoredResult() {
        UUID id = UUID.randomUUID();
        BackendServiceResponse first = restTemplate.getForObject(
                "/backend-service?verificationId={id}&query={query}",
                BackendServiceResponse.class,
                id,
                "CJQ"
        );

        BackendServiceResponse second = restTemplate.getForObject(
                "/backend-service?verificationId={id}&query={query}",
                BackendServiceResponse.class,
                id,
                "CJQ"
        );

        assertThat(first).isNotNull();
        assertThat(second).isEqualTo(first);
    }

    @Test
    void returnsBadRequestForInvalidVerificationIdFormatOnRead() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/verifications/{id}",
                String.class,
                "not-a-uuid"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid verificationId format");
    }
}
