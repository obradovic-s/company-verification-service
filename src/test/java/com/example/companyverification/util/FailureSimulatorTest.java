package com.example.companyverification.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FailureSimulatorTest {

    private final FailureSimulator simulator = new FailureSimulator();

    @Test
    void alwaysFailsWhenRateIsHundred() {
        boolean result = simulator.shouldFail(100);
        assertThat(result).isTrue();
    }

    @Test
    void neverFailsWhenRateIsZero() {
        boolean result = simulator.shouldFail(0);
        assertThat(result).isFalse();
    }
}
