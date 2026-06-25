package com.drivingschool.decorator;

import com.drivingschool.model.Course;

public abstract class CourseDecorator extends Course {
    protected Course decoratedCourse;

    public CourseDecorator(Course decoratedCourse, String name, double price) {
        super(name, price, 0);
        this.decoratedCourse = decoratedCourse;
    }

    @Override
    public int getDuration() {
        return decoratedCourse.getDuration();
    }
}
