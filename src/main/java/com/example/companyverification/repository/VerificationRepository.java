package com.example.companyverification.repository;

import com.example.companyverification.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<VerificationEntity, UUID> {
}
