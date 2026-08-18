package com.sunrisedental.service.strategy;

/**
 * Concrete Strategy: SeniorCitizenDiscountStrategy (10% discount)
 */
public class SeniorCitizenDiscountStrategy implements DiscountStrategy {

    private static final double PERCENTAGE = 10.0;

    @Override
    public double calculateDiscount(double subtotal) {
        if (subtotal <= 0) return 0.0;
        return Math.round((subtotal * (PERCENTAGE / 100.0)) * 100.0) / 100.0;
    }

    @Override
    public String getStrategyName() {
        return "Senior Citizen (10% Off)";
    }

    @Override
    public double getPercentage() {
        return PERCENTAGE;
    }
}
