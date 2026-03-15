package com.example.companyverification.controller.mock;

import com.example.companyverification.model.external.FreeApiCompany;
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
@RequestMapping("/free-third-party")
public class FreeThirdPartyController {

    private final DataFileLoader dataFileLoader;
    private final FailureSimulator failureSimulator;
    private final int failureRate;
    private final String dataFile;

    private List<FreeApiCompany> companies;

    public FreeThirdPartyController(
            DataFileLoader dataFileLoader,
            FailureSimulator failureSimulator,
            @Value("${app.failure-rate.free}") int failureRate,
            @Value("${app.data.free-file}") String dataFile
    ) {
        this.dataFileLoader = dataFileLoader;
        this.failureSimulator = failureSimulator;
        this.failureRate = failureRate;
        this.dataFile = dataFile;
    }

    @PostConstruct
    public void init() {
        this.companies = dataFileLoader.loadList(dataFile, FreeApiCompany[].class);
    }

    @GetMapping
    public List<FreeApiCompany> search(@RequestParam("query") String query) {
        if (failureSimulator.shouldFail(failureRate)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "FREE service unavailable");
        }

        String normalizedQuery = query.toLowerCase();
        return companies.stream()
                .filter(company -> company.cin().toLowerCase().contains(normalizedQuery))
                .toList();
    }
}
