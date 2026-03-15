package com.example.companyverification.controller.mock;

import com.example.companyverification.model.external.PremiumApiCompany;
import com.example.companyverification.util.DataFileLoader;
import com.example.companyverification.util.FailureSimulator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/premium-third-party")
public class PremiumThirdPartyController {

    private final DataFileLoader dataFileLoader;
    private final FailureSimulator failureSimulator;
    private final int failureRate;
    private final String dataFile;

    private List<PremiumApiCompany> companies;

    public PremiumThirdPartyController(
            DataFileLoader dataFileLoader,
            FailureSimulator failureSimulator,
            @Value("${app.failure-rate.premium}") int failureRate,
            @Value("${app.data.premium-file}") String dataFile
    ) {
        this.dataFileLoader = dataFileLoader;
        this.failureSimulator = failureSimulator;
        this.failureRate = failureRate;
        this.dataFile = dataFile;
    }

    @PostConstruct
    public void init() {
        this.companies = dataFileLoader.loadList(dataFile, PremiumApiCompany[].class);
    }

    @GetMapping
    public List<PremiumApiCompany> search(@RequestParam("query") String query) {
        if (failureSimulator.shouldFail(failureRate)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "PREMIUM service unavailable");
        }

        String normalizedQuery = query.toLowerCase();
        return companies.stream()
                .filter(company -> company.companyIdentificationNumber().toLowerCase().contains(normalizedQuery))
                .toList();
    }
}
