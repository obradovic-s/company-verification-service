package com.example.companyverification.model.dto;

import com.example.companyverification.model.domain.VerificationSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VerificationResponse(
        UUID verificationId,
        String query,
        Instant timestamp,
        VerificationStatus status,
        VerificationSource source,
        CompanyResult result,
        List<CompanyResult> otherResults
) {
}
