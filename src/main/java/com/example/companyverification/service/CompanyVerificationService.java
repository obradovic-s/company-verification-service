package com.example.companyverification.service;

import com.example.companyverification.exception.ThirdPartyServiceException;
import com.example.companyverification.mapper.CompanyMapper;
import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.domain.VerificationSource;
import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.CompanyResult;
import com.example.companyverification.model.dto.VerificationStatus;
import com.example.companyverification.service.client.ThirdPartyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyVerificationService {

    private static final Logger log = LoggerFactory.getLogger(CompanyVerificationService.class);

    private final ThirdPartyClient freeThirdPartyClient;
    private final ThirdPartyClient premiumThirdPartyClient;
    private final VerificationStoragePort verificationStorageService;
    private final CompanyMapper companyMapper;

    public CompanyVerificationService(
            @Qualifier("freeThirdPartyClient") ThirdPartyClient freeThirdPartyClient,
            @Qualifier("premiumThirdPartyClient") ThirdPartyClient premiumThirdPartyClient,
            VerificationStoragePort verificationStorageService,
            CompanyMapper companyMapper
    ) {
        this.freeThirdPartyClient = freeThirdPartyClient;
        this.premiumThirdPartyClient = premiumThirdPartyClient;
        this.verificationStorageService = verificationStorageService;
        this.companyMapper = companyMapper;
    }

    public BackendServiceResponse verify(UUID verificationId, String query) {
        return verificationStorageService.findStoredVerification(verificationId)
                .map(stored -> {
                    log.info("Returning stored verification verificationId={} status={}", verificationId, stored.status());
                    return stored;
                })
                .orElseGet(() -> processVerification(verificationId, query));
    }

    private BackendServiceResponse processVerification(UUID verificationId, String query) {
        log.info("Processing verification verificationId={} query={}", verificationId, query);

        boolean freeUnavailable = false;
        List<Company> freeActiveCompanies = Collections.emptyList();

        try {
            freeActiveCompanies = activeOnly(freeThirdPartyClient.search(query));
        } catch (ThirdPartyServiceException ex) {
            log.warn("FREE service unavailable verificationId={}", verificationId);
            freeUnavailable = true;
        }

        if (!freeActiveCompanies.isEmpty()) {
            log.info("Verification resolved verificationId={} source=FREE matchCount={}", verificationId, freeActiveCompanies.size());
            BackendServiceResponse response = buildFoundResponse(
                    verificationId,
                    query,
                    VerificationSource.FREE,
                    freeActiveCompanies
            );
            return verificationStorageService.save(verificationId, query, response);
        }

        boolean premiumUnavailable = false;
        List<Company> premiumActiveCompanies = Collections.emptyList();
        try {
            premiumActiveCompanies = activeOnly(premiumThirdPartyClient.search(query));
        } catch (ThirdPartyServiceException ex) {
            log.warn("PREMIUM service unavailable verificationId={}", verificationId);
            premiumUnavailable = true;
        }

        if (!premiumActiveCompanies.isEmpty()) {
            log.info("Verification resolved verificationId={} source=PREMIUM matchCount={}", verificationId, premiumActiveCompanies.size());
            BackendServiceResponse response = buildFoundResponse(
                    verificationId,
                    query,
                    VerificationSource.PREMIUM,
                    premiumActiveCompanies
            );
            return verificationStorageService.save(verificationId, query, response);
        }

        BackendServiceResponse response;
        if (freeUnavailable && premiumUnavailable) {
            log.error("Both third-party services unavailable verificationId={}", verificationId);
            response = new BackendServiceResponse(
                    verificationId,
                    query,
                    VerificationStatus.THIRD_PARTIES_DOWN,
                    null,
                    null,
                    List.of()
            );
        } else {
            log.info("No active results found verificationId={}", verificationId);
            VerificationSource source = !premiumUnavailable ? VerificationSource.PREMIUM : VerificationSource.FREE;
            response = new BackendServiceResponse(
                    verificationId,
                    query,
                    VerificationStatus.NO_RESULTS,
                    source,
                    null,
                    List.of()
            );
        }

        return verificationStorageService.save(verificationId, query, response);
    }

    private BackendServiceResponse buildFoundResponse(
            UUID verificationId,
            String query,
            VerificationSource source,
            List<Company> companies
    ) {
        CompanyResult first = companyMapper.toResult(companies.getFirst());
        List<CompanyResult> others = companies.stream()
                .skip(1)
                .map(companyMapper::toResult)
                .toList();

        return new BackendServiceResponse(
                verificationId,
                query,
                VerificationStatus.FOUND,
                source,
                first,
                others
        );
    }

    private List<Company> activeOnly(List<Company> companies) {
        return companies.stream().filter(Company::isActive).toList();
    }
}
