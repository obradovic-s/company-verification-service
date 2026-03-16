package com.example.companyverification.entity;

import com.example.companyverification.model.domain.VerificationSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verifications")
public class VerificationEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID verificationId;

    @Column(nullable = false)
    private String queryText;

    @Column(nullable = false)
    private Instant timestamp;

    @Lob
    @Column(nullable = false)
    private String resultJson;

    @Enumerated(EnumType.STRING)
    private VerificationSource source;

    public UUID getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(UUID verificationId) {
        this.verificationId = verificationId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public VerificationSource getSource() {
        return source;
    }

    public void setSource(VerificationSource source) {
        this.source = source;
    }
}
