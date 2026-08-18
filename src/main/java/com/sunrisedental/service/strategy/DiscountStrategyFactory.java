package com.sunrisedental.service.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory Pattern: DiscountStrategyFactory
 * Instantiates the appropriate DiscountStrategy implementation.
 */
public class DiscountStrategyFactory {

    private static final Map<String, DiscountStrategy> strategies = new HashMap<>();

    static {
        strategies.put("STANDARD", new StandardDiscountStrategy());
        strategies.put("SENIOR_CITIZEN", new SeniorCitizenDiscountStrategy());
        strategies.put("INSURANCE", new InsuranceDiscountStrategy());
        strategies.put("LOYALTY", new LoyaltyDiscountStrategy());
    }

    public static DiscountStrategy getStrategy(String discountType) {
        if (discountType == null || discountType.trim().isEmpty()) {
            return strategies.get("STANDARD");
        }
        
        String key = discountType.trim().toUpperCase().replace(" ", "_");
        DiscountStrategy strategy = strategies.get(key);
        if (strategy == null) {
            // Check contains
            if (key.contains("SENIOR")) return strategies.get("SENIOR_CITIZEN");
            if (key.contains("INSUR")) return strategies.get("INSURANCE");
            if (key.contains("LOYAL")) return strategies.get("LOYALTY");
            return strategies.get("STANDARD");
        }
        return strategy;
    }
}
