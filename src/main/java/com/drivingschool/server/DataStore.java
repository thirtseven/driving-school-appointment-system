package com.drivingschool.server;

import com.drivingschool.model.*;
import com.drivingschool.model.Student.StudentType;
import com.drivingschool.model.Appointment.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DataStore {
    private static volatile DataStore instance;
    
    private final Map<String, Appointment> appointments;
    private final Map<String, Student> students;
    private final AtomicInteger appointmentCounter;
    private final AtomicInteger studentCounter;
    
    private DataStore() {
        appointments = new ConcurrentHashMap<>();
        students = new ConcurrentHashMap<>();
        appointmentCounter = new AtomicInteger(1);
        studentCounter = new AtomicInteger(1);
        
        initSampleData();
    }
    
    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (DataStore.class) {
                if (instance == null) {
                    instance = new DataStore();
                }
            }
        }
        return instance;
    }
    
    private void initSampleData() {
        addStudent("张三", "13800138001", StudentType.STUDENT);
        addStudent("李四", "13800138002", StudentType.REGULAR);
        addStudent("王五", "13800138003", StudentType.GROUP);
        addStudent("赵六", "13800138004", StudentType.STUDENT);
    }
    
    public Student addStudent(String name, String phone, StudentType type) {
        String id = "STU" + String.format("%03d", studentCounter.getAndIncrement());
        Student student = new Student(id, name, phone, type);
        students.put(id, student);
        return student;
    }
    
    public Optional<Student> getStudent(String id) {
        return Optional.ofNullable(students.get(id));
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    public Optional<Student> updateStudent(String id, String name, String phone, StudentType type) {
        Student student = students.get(id);
        if (student != null) {
            Student updated = new Student(id, name, phone, type);
            students.put(id, updated);
            return Optional.of(updated);
        }
        return Optional.empty();
    }
    
    public boolean deleteStudent(String id) {
        return students.remove(id) != null;
    }
    
    public Appointment addAppointment(Student student, Course course, LocalDateTime classTime) {
        String id = "APT" + String.format("%06d", appointmentCounter.getAndIncrement());
        Appointment appointment = new Appointment(id, student, course, LocalDateTime.now(), classTime);
        appointment.setFinalPrice(course.calculatePrice());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointments.put(id, appointment);
        return appointment;
    }
    
    public Optional<Appointment> getAppointment(String id) {
        return Optional.ofNullable(appointments.get(id));
    }
    
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }
    
    public Optional<Appointment> updateAppointmentStatus(String id, AppointmentStatus status) {
        Appointment appointment = appointments.get(id);
        if (appointment != null) {
            appointment.setStatus(status);
            return Optional.of(appointment);
        }
        return Optional.empty();
    }
    
    public boolean deleteAppointment(String id) {
        return appointments.remove(id) != null;
    }
    
    public int getAppointmentCount() {
        return appointments.size();
    }
    
    public int getStudentCount() {
        return students.size();
    }
    
    public double getTotalRevenue() {
        return appointments.values().stream()
            .mapToDouble(Appointment::getFinalPrice)
            .sum();
    }
}
