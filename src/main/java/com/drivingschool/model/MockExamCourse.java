package com.drivingschool.model;

public class MockExamCourse extends Course {
    public MockExamCourse() {
        super("模拟考试", 500.0, 2);
    }

    @Override
    public double calculatePrice() {
        return getBasePrice();
    }

    @Override
    public String getDescription() {
        return "模拟考试 - 全真模拟考试环境，熟悉考试流程，提高考试通过率";
    }
}
