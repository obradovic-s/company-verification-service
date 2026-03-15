package com.example.companyverification.model.domain;

import java.time.LocalDate;

public record Company(
        String cin,
        String name,
        LocalDate registrationDate,
        String address,
        boolean isActive
) {
}
