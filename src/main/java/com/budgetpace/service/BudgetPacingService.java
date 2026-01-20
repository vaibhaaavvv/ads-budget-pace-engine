package com.budgetpace.service;

import com.budgetpace.dto.EligibilityResponse;
import com.budgetpace.model.Ad;
import com.budgetpace.model.Campaign;
import com.budgetpace.model.CampaignCache;
import com.budgetpace.repository.AdRepository;
import com.budgetpace.repository.CampaignRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BudgetPacingService {
    private final CampaignRepository campaignRepository;
    private final AdRepository adRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Set<String> processedRequests = ConcurrentHashMap.newKeySet();

    public BudgetPacingService(CampaignRepository campaignRepository, AdRepository adRepository, 
                               RedisTemplate<String, Object> redisTemplate) {
        this.campaignRepository = campaignRepository;
        this.adRepository = adRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void loadFromDatabase() {
        campaignRepository.findAll().forEach(campaign -> {
            CampaignCache cache = new CampaignCache(campaign.getCampaignId(), campaign.getDailySpendLimit());
            redisTemplate.opsForValue().set("campaign:" + campaign.getCampaignId(), cache);
        });
        adRepository.findAll().forEach(ad -> 
            redisTemplate.opsForValue().set("ad:" + ad.getAdId(), ad.getCampaignId())
        );
    }

    public EligibilityResponse checkEligibility(String adId, long cost, String requestId) {
        if (requestId != null && !processedRequests.add(requestId)) {
            return getCachedResponse(adId);
        }

        String campaignId = (String) redisTemplate.opsForValue().get("ad:" + adId);
        if (campaignId == null) {
            return new EligibilityResponse(false, 0, null);
        }

        CampaignCache cache = (CampaignCache) redisTemplate.opsForValue().get("campaign:" + campaignId);
        if (cache == null) {
            return new EligibilityResponse(false, 0, null);
        }

        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign == null || campaign.isExpired()) {
            return new EligibilityResponse(false, 0, null);
        }

        if (cache.needsReset()) {
            cache.resetBudget();
            redisTemplate.opsForValue().set("campaign:" + campaignId, cache);
        }

        synchronized (cache) {
            long remaining = cache.getRemainingBudget() - cost;
            
            if (remaining < 0) {
                return new EligibilityResponse(false, cache.getRemainingBudget(), campaign.getExpiryDate());
            }

            cache.setRemainingBudget(remaining);
            redisTemplate.opsForValue().set("campaign:" + campaignId, cache);

            return new EligibilityResponse(true, remaining, campaign.getExpiryDate());
        }
    }

    private EligibilityResponse getCachedResponse(String adId) {
        String campaignId = (String) redisTemplate.opsForValue().get("ad:" + adId);
        if (campaignId == null) return new EligibilityResponse(false, 0, null);
        
        CampaignCache cache = (CampaignCache) redisTemplate.opsForValue().get("campaign:" + campaignId);
        if (cache == null) return new EligibilityResponse(false, 0, null);
        
        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign == null) return new EligibilityResponse(false, 0, null);
        
        return new EligibilityResponse(true, cache.getRemainingBudget(), campaign.getExpiryDate());
    }

    public void syncCampaignUpdates() {
        campaignRepository.findAll().forEach(campaign -> {
            CampaignCache cache = (CampaignCache) redisTemplate.opsForValue().get("campaign:" + campaign.getCampaignId());
            if (cache != null && cache.getDailySpendLimit() != campaign.getDailySpendLimit()) {
                cache.setDailySpendLimit(campaign.getDailySpendLimit());
                redisTemplate.opsForValue().set("campaign:" + campaign.getCampaignId(), cache);
            }
        });
    }

    public void resetDailyBudgets() {
        campaignRepository.findAll().forEach(campaign -> {
            CampaignCache cache = (CampaignCache) redisTemplate.opsForValue().get("campaign:" + campaign.getCampaignId());
            if (cache != null) {
                cache.resetBudget();
                redisTemplate.opsForValue().set("campaign:" + campaign.getCampaignId(), cache);
            }
        });
    }

    public void cleanupExpiredCampaigns() {
        campaignRepository.findAll().forEach(campaign -> {
            if (campaign.isExpired()) {
                redisTemplate.delete("campaign:" + campaign.getCampaignId());
            }
        });
    }
}
