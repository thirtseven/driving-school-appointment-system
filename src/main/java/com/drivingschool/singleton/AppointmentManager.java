package com.drivingschool.singleton;

import com.drivingschool.model.Appointment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentManager {
    private static volatile AppointmentManager instance;
    private final List<Appointment> appointments;

    private AppointmentManager() {
        this.appointments = new ArrayList<>();
    }

    public static AppointmentManager getInstance() {
        if (instance == null) {
            synchronized (AppointmentManager.class) {
                if (instance == null) {
                    instance = new AppointmentManager();
                }
            }
        }
        return instance;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        System.out.println("[预约管理中心] 新增预约: " + appointment.getId());
    }

    public Optional<Appointment> findAppointment(String appointmentId) {
        return appointments.stream()
                .filter(a -> a.getId().equals(appointmentId))
                .findFirst();
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public int getAppointmentCount() {
        return appointments.size();
    }

    public void printAllAppointments() {
        System.out.println("\n========== 预约记录列表 ==========");
        if (appointments.isEmpty()) {
            System.out.println("暂无预约记录");
        } else {
            appointments.forEach(System.out::println);
        }
        System.out.println("==================================\n");
    }
}
