package com.drivingschool.model;

/**
 * 练车课程抽象基类
 * 使用装饰器模式的基础组件
 */
public abstract class Course {
    protected String name;
    protected double basePrice;
    protected int duration; // 课时（小时）

    public Course(String name, double basePrice, int duration) {
        this.name = name;
        this.basePrice = basePrice;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * 获取课程总费用（可被装饰器覆盖）
     */
    public abstract double calculatePrice();

    /**
     * 获取课程详细信息
     */
    public abstract String getDescription();

    @Override
    public String toString() {
        return String.format("课程: %s | 基础价格: %.2f元 | 课时: %d小时 | 总费用: %.2f元",
                name, basePrice, duration, calculatePrice());
    }
}
