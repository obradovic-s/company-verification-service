package com.example.companyverification.service;

import com.example.companyverification.model.dto.BackendServiceResponse;

import java.util.Optional;
import java.util.UUID;

public interface VerificationStoragePort {

    Optional<BackendServiceResponse> findStoredVerification(UUID verificationId);

    BackendServiceResponse save(UUID verificationId, String query, BackendServiceResponse response);
}
