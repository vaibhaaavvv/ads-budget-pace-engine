package com.budgetpace.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ads")
public class Ad implements Serializable {
    @Id
    private String adId;
    
    private String campaignId;

    public Ad() {}

    public Ad(String adId, String campaignId) {
        this.adId = adId;
        this.campaignId = campaignId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }
}
