package com.example.companyverification.mapper;

import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.external.FreeApiCompany;
import com.example.companyverification.model.external.PremiumApiCompany;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest {

    private final CompanyMapper mapper = new CompanyMapper();

    @Test
    void mapsFreeCompanyToCanonicalModel() {
        FreeApiCompany free = new FreeApiCompany(
                "ABC123",
                "Acme",
                LocalDate.of(2020, 1, 10),
                "Main St",
                true
        );

        Company mapped = mapper.fromFree(free);

        assertThat(mapped.cin()).isEqualTo("ABC123");
        assertThat(mapped.name()).isEqualTo("Acme");
        assertThat(mapped.registrationDate()).isEqualTo(LocalDate.of(2020, 1, 10));
        assertThat(mapped.address()).isEqualTo("Main St");
        assertThat(mapped.isActive()).isTrue();
    }

    @Test
    void mapsPremiumCompanyToCanonicalModel() {
        PremiumApiCompany premium = new PremiumApiCompany(
                "XYZ999",
                "Globex",
                LocalDate.of(2018, 2, 5),
                "Broadway",
                false
        );

        Company mapped = mapper.fromPremium(premium);

        assertThat(mapped.cin()).isEqualTo("XYZ999");
        assertThat(mapped.name()).isEqualTo("Globex");
        assertThat(mapped.registrationDate()).isEqualTo(LocalDate.of(2018, 2, 5));
        assertThat(mapped.address()).isEqualTo("Broadway");
        assertThat(mapped.isActive()).isFalse();
    }
}
