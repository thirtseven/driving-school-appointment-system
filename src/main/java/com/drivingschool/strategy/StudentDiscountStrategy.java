package com.drivingschool.strategy;

public class StudentDiscountStrategy implements DiscountStrategy {
    private static final double DISCOUNT_RATE = 0.8;

    @Override
    public double calculateDiscount(double originalPrice) {
        return originalPrice * DISCOUNT_RATE;
    }

    @Override
    public String getStrategyName() {
        return "学生学员（8折优惠）";
    }
}
