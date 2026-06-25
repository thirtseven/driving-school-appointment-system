package com.drivingschool.decorator;

import com.drivingschool.model.Course;

public class SprintCoaching extends CourseDecorator {
    public SprintCoaching(Course course) {
        super(course, "考前冲刺辅导", 200.0);
    }

    @Override
    public double calculatePrice() {
        return decoratedCourse.calculatePrice() + getBasePrice();
    }

    @Override
    public String getDescription() {
        return decoratedCourse.getDescription() + " + 考前冲刺辅导（考前重点强化训练）";
    }

    @Override
    public String toString() {
        return String.format("%s + [考前冲刺辅导: +%.2f元] | 总费用: %.2f元",
                decoratedCourse.getName(), getBasePrice(), calculatePrice());
    }
}
