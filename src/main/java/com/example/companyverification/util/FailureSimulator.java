package com.example.companyverification.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class FailureSimulator {

    public boolean shouldFail(int failureRatePercent) {
        return ThreadLocalRandom.current().nextInt(100) < failureRatePercent;
    }
}
