package com.drivingschool.model;

import com.drivingschool.observer.Observer;
import com.drivingschool.observer.Subject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Appointment implements Subject {
    private String id;
    private Student student;
    private Course course;
    private LocalDateTime appointmentTime;
    private LocalDateTime classTime;
    private double finalPrice;
    private AppointmentStatus status;
    private List<Observer> observers;

    public enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED
    }

    public Appointment(String id, Student student, Course course, LocalDateTime appointmentTime, LocalDateTime classTime) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.appointmentTime = appointmentTime;
        this.classTime = classTime;
        this.status = AppointmentStatus.PENDING;
        this.observers = new ArrayList<>();
    }

    public String getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public LocalDateTime getClassTime() { return classTime; }
    public double getFinalPrice() { return finalPrice; }
    public AppointmentStatus getStatus() { return status; }

    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getFormattedAppointmentTime() {
        return appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedClassTime() {
        return classTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("预约[%s] %s - %s | 练车时间: %s | 状态: %s | 费用: %.2f元",
                id, student.getName(), course.getName(),
                getFormattedClassTime(), getStatusName(), finalPrice);
    }

    private String getStatusName() {
        switch (status) {
            case PENDING: return "待确认";
            case CONFIRMED: return "已确认";
            case COMPLETED: return "已完成";
            case CANCELLED: return "已取消";
            default: return "未知";
        }
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
        System.out.println("[观察者模式] 注册通知渠道: " + observer.getObserverName());
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
        System.out.println("[观察者模式] 移除通知渠道: " + observer.getObserverName());
    }

    @Override
    public void notifyObservers(String message) {
        System.out.println("\n[观察者模式] 触发通知 - 预约ID: " + id);
        for (Observer observer : observers) {
            observer.update(this, message);
        }
    }
}
