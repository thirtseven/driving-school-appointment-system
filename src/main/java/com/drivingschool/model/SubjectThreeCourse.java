package com.drivingschool.model;

public class SubjectThreeCourse extends Course {
    public SubjectThreeCourse() {
        super("科三上路", 3200.0, 16);
    }

    @Override
    public double calculatePrice() {
        return getBasePrice();
    }

    @Override
    public String getDescription() {
        return "科目三上路培训 - 包含上车准备、模拟灯光、起步、直线行驶、加减挡位操作、变更车道、靠边停车等考试项目";
    }
}
