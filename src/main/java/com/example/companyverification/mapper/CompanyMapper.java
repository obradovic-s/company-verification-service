package com.example.companyverification.mapper;

import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.dto.CompanyResult;
import com.example.companyverification.model.external.FreeApiCompany;
import com.example.companyverification.model.external.PremiumApiCompany;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public Company fromFree(FreeApiCompany company) {
        return new Company(
                company.cin(),
                company.name(),
                company.registrationDate(),
                company.address(),
                company.isActive()
        );
    }

    public Company fromPremium(PremiumApiCompany company) {
        return new Company(
                company.companyIdentificationNumber(),
                company.companyName(),
                company.registrationDate(),
                company.companyFullAddress(),
                company.isActive()
        );
    }

    public CompanyResult toResult(Company company) {
        return new CompanyResult(
                company.cin(),
                company.name(),
                company.registrationDate(),
                company.address()
        );
    }
}
