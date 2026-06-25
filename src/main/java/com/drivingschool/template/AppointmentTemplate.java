package com.drivingschool.template;

import com.drivingschool.model.Appointment;
import com.drivingschool.model.Course;
import com.drivingschool.model.Student;
import com.drivingschool.strategy.DiscountContext;
import com.drivingschool.strategy.DiscountStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AppointmentTemplate {
    public final Appointment executeAppointment(Student student, Course course, LocalDateTime classTime) {
        System.out.println("\n========== 开始预约流程 ==========");

        String appointmentId = generateAppointmentId();
        Appointment appointment = new Appointment(appointmentId, student, course, LocalDateTime.now(), classTime);

        registerInformation(appointment);
        double finalPrice = calculateFee(appointment);
        assignCoach(appointment);

        appointment.setFinalPrice(finalPrice);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);

        System.out.println("========== 预约流程完成 ==========\n");
        return appointment;
    }

    protected abstract void registerInformation(Appointment appointment);

    protected abstract double calculateFee(Appointment appointment);

    protected abstract void assignCoach(Appointment appointment);

    protected String generateAppointmentId() {
        return "APT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
