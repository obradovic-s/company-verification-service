package com.example.companyverification.integration;

import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationStatus;
import com.example.companyverification.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "app.failure-rate.free=100",
                "app.failure-rate.premium=100"
        }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ThirdPartiesDownIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VerificationRepository verificationRepository;

    @BeforeEach
    void setup() {
        verificationRepository.deleteAll();
    }

    @Test
    void returnsThirdPartiesDownWhenBothFail() {
        UUID id = UUID.randomUUID();
        ResponseEntity<BackendServiceResponse> entity = restTemplate.getForEntity(
                "/backend-service?verificationId={id}&query={query}",
                BackendServiceResponse.class,
                id,
                "CJQ"
        );

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getBody().status()).isEqualTo(VerificationStatus.THIRD_PARTIES_DOWN);
    }
}
