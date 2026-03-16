package com.example.companyverification.service;

import com.example.companyverification.exception.ThirdPartyServiceException;
import com.example.companyverification.mapper.CompanyMapper;
import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.domain.VerificationSource;
import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationStatus;
import com.example.companyverification.service.client.ThirdPartyClient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyVerificationServiceTest {

    @Test
    void returnsStoredResultWhenVerificationAlreadyExists() {
        UUID id = UUID.randomUUID();
        InMemoryStorage storage = new InMemoryStorage();
        BackendServiceResponse stored = new BackendServiceResponse(id, "A", VerificationStatus.NO_RESULTS, VerificationSource.FREE, null, List.of());
        storage.byId.put(id, stored);

        CompanyVerificationService service = new CompanyVerificationService(
                query -> List.of(),
                query -> List.of(),
                storage,
                new CompanyMapper()
        );

        BackendServiceResponse response = service.verify(id, "A");

        assertThat(response).isEqualTo(stored);
    }

    @Test
    void usesFreeWhenFreeHasActiveResults() {
        UUID id = UUID.randomUUID();
        InMemoryStorage storage = new InMemoryStorage();

        ThirdPartyClient free = query -> List.of(
                new Company("ABC1", "Active Co", LocalDate.parse("2020-01-01"), "Addr", true),
                new Company("ABC2", "Other Co", LocalDate.parse("2020-01-02"), "Addr2", true)
        );
        ThirdPartyClient premium = query -> List.of();

        CompanyVerificationService service = new CompanyVerificationService(free, premium, storage, new CompanyMapper());

        BackendServiceResponse response = service.verify(id, "ABC");

        assertThat(response.status()).isEqualTo(VerificationStatus.FOUND);
        assertThat(response.source()).isEqualTo(VerificationSource.FREE);
        assertThat(response.result().cin()).isEqualTo("ABC1");
        assertThat(response.otherResults()).hasSize(1);
    }

    @Test
    void fallsBackToPremiumWhenFreeUnavailable() {
        UUID id = UUID.randomUUID();
        InMemoryStorage storage = new InMemoryStorage();

        ThirdPartyClient free = query -> {
            throw new ThirdPartyServiceException("down");
        };
        ThirdPartyClient premium = query -> List.of(
                new Company("ABC9", "Premium Co", LocalDate.parse("2019-01-01"), "Prem", true)
        );

        CompanyVerificationService service = new CompanyVerificationService(free, premium, storage, new CompanyMapper());

        BackendServiceResponse response = service.verify(id, "ABC");

        assertThat(response.status()).isEqualTo(VerificationStatus.FOUND);
        assertThat(response.source()).isEqualTo(VerificationSource.PREMIUM);
    }

    @Test
    void returnsThirdPartiesDownWhenBothUnavailable() {
        UUID id = UUID.randomUUID();
        InMemoryStorage storage = new InMemoryStorage();

        ThirdPartyClient free = query -> {
            throw new ThirdPartyServiceException("down");
        };
        ThirdPartyClient premium = query -> {
            throw new ThirdPartyServiceException("down");
        };

        CompanyVerificationService service = new CompanyVerificationService(free, premium, storage, new CompanyMapper());

        BackendServiceResponse response = service.verify(id, "ABC");

        assertThat(response.status()).isEqualTo(VerificationStatus.THIRD_PARTIES_DOWN);
        assertThat(response.source()).isNull();
    }

    private static class InMemoryStorage implements VerificationStoragePort {
        private final Map<UUID, BackendServiceResponse> byId = new HashMap<>();

        @Override
        public Optional<BackendServiceResponse> findStoredVerification(UUID verificationId) {
            return Optional.ofNullable(byId.get(verificationId));
        }

        @Override
        public BackendServiceResponse save(UUID verificationId, String query, BackendServiceResponse response) {
            byId.put(verificationId, response);
            return response;
        }
    }
}
