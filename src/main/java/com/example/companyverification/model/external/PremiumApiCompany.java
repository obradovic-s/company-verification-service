package com.example.companyverification.model.external;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record PremiumApiCompany(
        String companyIdentificationNumber,
        String companyName,
        LocalDate registrationDate,
        @JsonProperty("companyFullAddress")
        @JsonAlias("fullAddress")
        String companyFullAddress,
        boolean isActive
) {
}
