package com.sunrisedental.service.strategy;

/**
 * Concrete Strategy: StandardDiscountStrategy (0% discount)
 */
public class StandardDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double subtotal) {
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Standard (No Discount)";
    }

    @Override
    public double getPercentage() {
        return 0.0;
    }
}
