package com.budgetpace.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class Campaign implements Serializable {
    @Id
    private String campaignId;
    
    private long dailySpendLimit;
    
    private LocalDate startDate;
    
    private LocalDate expiryDate;

    public Campaign() {}

    public Campaign(String campaignId, long dailySpendLimit, LocalDate startDate, LocalDate expiryDate) {
        this.campaignId = campaignId;
        this.dailySpendLimit = dailySpendLimit;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}
