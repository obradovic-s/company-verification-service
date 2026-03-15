package com.example.companyverification.model.dto;

import com.example.companyverification.model.domain.VerificationSource;

import java.util.List;
import java.util.UUID;

public record BackendServiceResponse(
        UUID verificationId,
        String query,
        VerificationStatus status,
        VerificationSource source,
        CompanyResult result,
        List<CompanyResult> otherResults
) {
}
