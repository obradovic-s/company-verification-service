package com.example.companyverification.controller;

import com.example.companyverification.exception.InvalidVerificationIdException;
import com.example.companyverification.model.dto.BackendServiceResponse;
import com.example.companyverification.model.dto.VerificationStatus;
import com.example.companyverification.service.CompanyVerificationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/backend-service")
public class BackendServiceController {

    private final CompanyVerificationService companyVerificationService;

    public BackendServiceController(CompanyVerificationService companyVerificationService) {
        this.companyVerificationService = companyVerificationService;
    }

    @GetMapping
    public ResponseEntity<BackendServiceResponse> verify(
            @RequestParam("verificationId") String verificationId,
            @RequestParam("query") @NotBlank String query
    ) {
        UUID parsedVerificationId = parseVerificationId(verificationId);
        BackendServiceResponse response = companyVerificationService.verify(parsedVerificationId, query);

        if (response.status() == VerificationStatus.THIRD_PARTIES_DOWN) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        return ResponseEntity.ok(response);
    }

    private UUID parseVerificationId(String verificationId) {
        try {
            return UUID.fromString(verificationId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidVerificationIdException("Invalid verificationId format. Must be a valid UUID.");
        }
    }
}
