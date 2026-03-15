package com.example.companyverification.model.external;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;

public record PremiumApiCompany(
        String companyIdentificationNumber,
        String companyName,
        LocalDate registrationDate,
        @JsonAlias({"fullAddress", "companyFullAddress"}) String companyFullAddress,
        boolean isActive
) {
}
