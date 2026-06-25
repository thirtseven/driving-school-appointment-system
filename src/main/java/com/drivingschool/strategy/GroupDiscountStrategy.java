package com.drivingschool.strategy;

public class GroupDiscountStrategy implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.7;

    @Override
    public double calculateDiscount(double originalPrice) {
        return originalPrice * DISCOUNT_RATE;
    }

    @Override
    public String getStrategyName() {
        return "团体学员（7折优惠）";
    }
}
