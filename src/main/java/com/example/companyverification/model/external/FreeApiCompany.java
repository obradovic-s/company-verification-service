package com.example.companyverification.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FreeApiCompany(
        String cin,
        String name,
        LocalDate registrationDate,
        String address,
        @JsonProperty("is_active")
        boolean isActive
) {
}
