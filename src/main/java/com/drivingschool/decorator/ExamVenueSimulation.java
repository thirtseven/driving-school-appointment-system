package com.drivingschool.decorator;

import com.drivingschool.model.Course;

public class ExamVenueSimulation extends CourseDecorator {
    public ExamVenueSimulation(Course course) {
        super(course, "考场模拟", 300.0);
    }

    @Override
    public double calculatePrice() {
        return decoratedCourse.calculatePrice() + getBasePrice();
    }

    @Override
    public String getDescription() {
        return decoratedCourse.getDescription() + " + 考场模拟服务（真实考场环境模拟训练）";
    }

    @Override
    public String toString() {
        return String.format("%s + [考场模拟: +%.2f元] | 总费用: %.2f元",
                decoratedCourse.getName(), getBasePrice(), calculatePrice());
    }
}
