package com.drivingschool.factory;

import com.drivingschool.model.Course;
import com.drivingschool.model.SubjectTwoCourse;

public class SubjectTwoFactory implements CourseFactory {
    @Override
    public Course createCourse() {
        return new SubjectTwoCourse();
    }
}
