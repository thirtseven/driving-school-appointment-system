package com.drivingschool.factory;

import com.drivingschool.model.Course;
import com.drivingschool.model.SubjectThreeCourse;

public class SubjectThreeFactory implements CourseFactory {
    @Override
    public Course createCourse() {
        return new SubjectThreeCourse();
    }
}
