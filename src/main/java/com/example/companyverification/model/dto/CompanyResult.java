package com.example.companyverification.model.dto;

import java.time.LocalDate;

public record CompanyResult(
        String cin,
        String name,
        LocalDate registrationDate,
        String address
) {
}
