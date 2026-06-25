package com.drivingschool.strategy;

import com.drivingschool.model.Student;

public class DiscountContext {
    private DiscountStrategy strategy;

    public DiscountContext(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double applyDiscount(double originalPrice) {
        return strategy.calculateDiscount(originalPrice);
    }

    public String getStrategyName() {
        return strategy.getStrategyName();
    }

    public static DiscountStrategy createStrategyByStudentType(Student.StudentType type) {
        switch (type) {
            case STUDENT:
                return new StudentDiscountStrategy();
            case GROUP:
                return new GroupDiscountStrategy();
            case REGULAR:
            default:
                return new RegularDiscountStrategy();
        }
    }
}
