package com.budgetpace.scheduler;

import com.budgetpace.service.BudgetPacingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BudgetScheduler {
    private final BudgetPacingService budgetPacingService;

    public BudgetScheduler(BudgetPacingService budgetPacingService) {
        this.budgetPacingService = budgetPacingService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyBudgets() {
        budgetPacingService.resetDailyBudgets();
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void cleanupExpiredCampaigns() {
        budgetPacingService.cleanupExpiredCampaigns();
    }

    @Scheduled(fixedRate = 300000)
    public void syncCampaignUpdates() {
        budgetPacingService.syncCampaignUpdates();
    }
}
