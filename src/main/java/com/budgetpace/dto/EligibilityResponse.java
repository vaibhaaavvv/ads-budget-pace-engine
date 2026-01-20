package com.budgetpace.dto;

import java.time.LocalDate;

public class EligibilityResponse {
    private boolean eligible;
    private long costRemaining;
    private LocalDate expiresAt;

    public EligibilityResponse(boolean eligible, long costRemaining, LocalDate expiresAt) {
        this.eligible = eligible;
        this.costRemaining = costRemaining;
        this.expiresAt = expiresAt;
    }

    public boolean isEligible() {
        return eligible;
    }

    public long getCostRemaining() {
        return costRemaining;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }
}
