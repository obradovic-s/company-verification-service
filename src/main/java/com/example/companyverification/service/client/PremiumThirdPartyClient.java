package com.example.companyverification.service.client;

import com.example.companyverification.exception.ThirdPartyServiceException;
import com.example.companyverification.mapper.CompanyMapper;
import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.external.PremiumApiCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class PremiumThirdPartyClient implements ThirdPartyClient {

    private static final Logger log = LoggerFactory.getLogger(PremiumThirdPartyClient.class);

    private static final ParameterizedTypeReference<List<PremiumApiCompany>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() { };

    private final RestClient restClient;
    private final CompanyMapper companyMapper;

    public PremiumThirdPartyClient(RestClient thirdPartyRestClient, CompanyMapper companyMapper) {
        this.restClient = thirdPartyRestClient;
        this.companyMapper = companyMapper;
    }

    @Override
    public List<Company> search(String query) throws ThirdPartyServiceException {
        log.debug("Calling PREMIUM third-party service query={}", query);
        List<PremiumApiCompany> rawCompanies = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/premium-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new ThirdPartyServiceException("PREMIUM service unavailable");
                        })
                .body(RESPONSE_TYPE);

        if (rawCompanies == null) {
            return Collections.emptyList();
        }

        log.debug("PREMIUM service returned resultCount={}", rawCompanies.size());
        return rawCompanies.stream().map(companyMapper::fromPremium).toList();
    }
}
