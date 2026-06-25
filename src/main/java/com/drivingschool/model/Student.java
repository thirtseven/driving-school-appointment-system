package com.drivingschool.model;

public class Student {
    private String id;
    private String name;
    private String phone;
    private StudentType type;

    public enum StudentType {
        REGULAR,
        STUDENT,
        GROUP
    }

    public Student(String id, String name, String phone, StudentType type) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.type = type;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public StudentType getType() { return type; }

    @Override
    public String toString() {
        return String.format("学员[%s] %s (%s) - %s", id, name, phone, getTypeName());
    }

    private String getTypeName() {
        switch (type) {
            case REGULAR: return "普通学员";
            case STUDENT: return "学生学员";
            case GROUP: return "团体学员";
            default: return "未知";
        }
    }
}
