package com.drivingschool.model;

public class SubjectTwoCourse extends Course {
    public SubjectTwoCourse() {
        super("科二基础", 2800.0, 12);
    }

    @Override
    public double calculatePrice() {
        return getBasePrice();
    }

    @Override
    public String getDescription() {
        return "科目二基础培训 - 包含倒车入库、侧方停车、坡道定点起步、直角转弯、曲线行驶五项必考内容";
    }
}
