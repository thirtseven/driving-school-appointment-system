package com.drivingschool.strategy;

public class RegularDiscountStrategy implements DiscountStrategy {
    @Override
    public double calculateDiscount(double originalPrice) {
        return originalPrice;
    }

    @Override
    public String getStrategyName() {
        return "普通学员（原价）";
    }
}
