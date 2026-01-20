package com.budgetpace.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CampaignCache implements Serializable {
    private String campaignId;
    private long dailySpendLimit;
    private long remainingBudget;
    private LocalDateTime lastUpdated;

    public CampaignCache() {}

    public CampaignCache(String campaignId, long dailySpendLimit) {
        this.campaignId = campaignId;
        this.dailySpendLimit = dailySpendLimit;
        this.remainingBudget = dailySpendLimit;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public long getDailySpendLimit() {
        return dailySpendLimit;
    }

    public void setDailySpendLimit(long dailySpendLimit) {
        this.dailySpendLimit = dailySpendLimit;
    }

    public long getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(long remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean needsReset() {
        return LocalDateTime.now().isAfter(lastUpdated.plusHours(24));
    }

    public void resetBudget() {
        this.remainingBudget = dailySpendLimit;
        this.lastUpdated = LocalDateTime.now();
    }
}
