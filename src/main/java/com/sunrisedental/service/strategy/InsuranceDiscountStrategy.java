package com.sunrisedental.service.strategy;

/**
 * Concrete Strategy: InsuranceDiscountStrategy (15% coverage/discount)
 */
public class InsuranceDiscountStrategy implements DiscountStrategy {

    private static final double PERCENTAGE = 15.0;

    @Override
    public double calculateDiscount(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return Math.round((subtotal * (PERCENTAGE / 100.0)) * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return "Corporate Insurance (15% Coverage)";
    }

    @Override
    public double getPercentage() {
        return PERCENTAGE;
    }
}
