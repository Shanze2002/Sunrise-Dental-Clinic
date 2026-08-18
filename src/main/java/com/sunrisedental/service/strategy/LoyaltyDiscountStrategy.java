package com.sunrisedental.service.strategy;

/**
 * Concrete Strategy: LoyaltyDiscountStrategy (5% discount)
 */
public class LoyaltyDiscountStrategy implements DiscountStrategy {

    private static final double PERCENTAGE = 5.0;

    @Override
    public double calculateDiscount(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return Math.round((subtotal * (PERCENTAGE / 100.0)) * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return "Loyalty Member (5% Off)";
    }

    @Override
    public double getPercentage() {
        return PERCENTAGE;
    }
}
