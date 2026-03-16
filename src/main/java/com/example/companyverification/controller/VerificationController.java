package com.example.companyverification.controller;

import com.example.companyverification.model.dto.VerificationResponse;
import com.example.companyverification.service.VerificationStorageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/verifications")
public class VerificationController {

    private final VerificationStorageService verificationStorageService;

    public VerificationController(VerificationStorageService verificationStorageService) {
        this.verificationStorageService = verificationStorageService;
    }

    @GetMapping("/{verificationId}")
    public VerificationResponse get(@PathVariable UUID verificationId) {
        return verificationStorageService.getVerification(verificationId);
    }
}
