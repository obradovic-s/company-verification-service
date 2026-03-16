package com.example.companyverification.integration;

import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationStatus;
import com.example.companyverification.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "app.failure-rate.free=100",
                "app.failure-rate.premium=0"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FallbackIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VerificationRepository verificationRepository;

    @BeforeEach
    void setup() {
        verificationRepository.deleteAll();
    }

    @Test
    void fallsBackToPremiumWhenFreeFails() {
        UUID id = UUID.randomUUID();
        BackendServiceResponse response = restTemplate.getForObject(
                "/backend-service?verificationId={id}&query={query}",
                BackendServiceResponse.class,
                id,
                "CJQ"
        );

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(VerificationStatus.FOUND);
        assertThat(response.source()).isNotNull();
        assertThat(response.source().name()).isEqualTo("PREMIUM");
    }
}
