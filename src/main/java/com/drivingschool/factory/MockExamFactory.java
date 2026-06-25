package com.drivingschool.factory;

import com.drivingschool.model.Course;
import com.drivingschool.model.MockExamCourse;

public class MockExamFactory implements CourseFactory {
    @Override
    public Course createCourse() {
        return new MockExamCourse();
    }
}
