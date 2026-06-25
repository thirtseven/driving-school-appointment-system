package com.drivingschool.strategy;

public interface DiscountStrategy {
    double calculateDiscount(double originalPrice);
    String getStrategyName();
}
