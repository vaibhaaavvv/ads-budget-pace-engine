package com.budgetpace.controller;

import com.budgetpace.dto.EligibilityRequest;
import com.budgetpace.dto.EligibilityResponse;
import com.budgetpace.service.BudgetPacingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final BudgetPacingService budgetPacingService;

    public BudgetController(BudgetPacingService budgetPacingService) {
        this.budgetPacingService = budgetPacingService;
    }

    @PostMapping("/check-eligibility")
    public EligibilityResponse checkEligibility(@RequestBody EligibilityRequest request) {
        return budgetPacingService.checkEligibility(
            request.getAdId(), 
            request.getCost(), 
            request.getRequestId()
        );
    }
}
