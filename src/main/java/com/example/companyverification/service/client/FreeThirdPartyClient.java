package com.example.companyverification.service.client;

import com.example.companyverification.exception.ThirdPartyServiceException;
import com.example.companyverification.mapper.CompanyMapper;
import com.example.companyverification.model.domain.Company;
import com.example.companyverification.model.external.FreeApiCompany;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class FreeThirdPartyClient implements ThirdPartyClient {

    private static final ParameterizedTypeReference<List<FreeApiCompany>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() { };

    private final RestClient restClient;
    private final CompanyMapper companyMapper;

    public FreeThirdPartyClient(RestClient thirdPartyRestClient, CompanyMapper companyMapper) {
        this.restClient = thirdPartyRestClient;
        this.companyMapper = companyMapper;
    }

    @Override
    public List<Company> search(String query) throws ThirdPartyServiceException {
        List<FreeApiCompany> rawCompanies = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/free-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new ThirdPartyServiceException("FREE service unavailable");
                        })
                .body(RESPONSE_TYPE);

        if (rawCompanies == null) {
            return Collections.emptyList();
        }

        return rawCompanies.stream().map(companyMapper::fromFree).toList();
    }
}
