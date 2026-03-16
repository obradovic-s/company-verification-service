package com.example.companyverification.service;

import com.example.companyverification.entity.VerificationEntity;
import com.example.companyverification.exception.VerificationNotFoundException;
import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationResponse;
import com.example.companyverification.repository.VerificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationStorageService implements VerificationStoragePort {

    private final VerificationRepository verificationRepository;
    private final ObjectMapper objectMapper;

    public VerificationStorageService(VerificationRepository verificationRepository, ObjectMapper objectMapper) {
        this.verificationRepository = verificationRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<BackendServiceResponse> findStoredVerification(UUID verificationId) {
        return verificationRepository.findById(verificationId)
                .map(this::toBackendResponse);
    }

    @Override
    public BackendServiceResponse save(UUID verificationId, String query, BackendServiceResponse response) {
        VerificationEntity entity = new VerificationEntity();
        entity.setVerificationId(verificationId);
        entity.setQueryText(query);
        entity.setTimestamp(Instant.now());
        entity.setSource(response.source());
        entity.setResultJson(toJson(response));
        verificationRepository.save(entity);
        return response;
    }

    public VerificationResponse getVerification(UUID verificationId) {
        VerificationEntity entity = verificationRepository.findById(verificationId)
                .orElseThrow(() -> new VerificationNotFoundException("Verification not found: " + verificationId));

        BackendServiceResponse response = toBackendResponse(entity);
        return new VerificationResponse(
                entity.getVerificationId(),
                entity.getQueryText(),
                entity.getTimestamp(),
                response.status(),
                response.source(),
                response.result(),
                response.otherResults()
        );
    }

    private BackendServiceResponse toBackendResponse(VerificationEntity entity) {
        try {
            return objectMapper.readValue(entity.getResultJson(), BackendServiceResponse.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not deserialize verification response", ex);
        }
    }

    private String toJson(BackendServiceResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize verification response", ex);
        }
    }
}
