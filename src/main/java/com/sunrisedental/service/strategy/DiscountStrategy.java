package com.sunrisedental.service.strategy;

/**
 * Strategy Pattern: DiscountStrategy
 * Defines the contract for dynamic billing discount calculation algorithms.
 */
public interface DiscountStrategy {
    
    /**
     * Calculates the monetary discount amount based on the subtotal.
     * @param subtotal Gross amount before discount (Consultation + Treatment + Additional)
     * @return Calculated discount amount in LKR
     */
    double calculateDiscount(double subtotal);

    /**
     * @return Human-readable name of the discount policy
     */
    String getStrategyName();

    /**
     * @return Discount percentage (e.g. 10.0 for 10%)
     */
    double getPercentage();
}
