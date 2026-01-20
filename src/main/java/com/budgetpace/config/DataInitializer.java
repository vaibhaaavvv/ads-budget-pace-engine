package com.budgetpace.config;

import com.budgetpace.model.Ad;
import com.budgetpace.model.Campaign;
import com.budgetpace.repository.AdRepository;
import com.budgetpace.repository.CampaignRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CampaignRepository campaignRepository;
    private final AdRepository adRepository;

    public DataInitializer(CampaignRepository campaignRepository, AdRepository adRepository) {
        this.campaignRepository = campaignRepository;
        this.adRepository = adRepository;
    }

    @Override
    public void run(String... args) {
        if (campaignRepository.count() == 0) {
            Campaign campaign1 = new Campaign("campaign1", 100, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 18));
            campaignRepository.save(campaign1);

            adRepository.save(new Ad("ad1", "campaign1"));
            adRepository.save(new Ad("ad2", "campaign1"));
            adRepository.save(new Ad("ad3", "campaign1"));
        }
    }
}
