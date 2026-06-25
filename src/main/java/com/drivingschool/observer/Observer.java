package com.drivingschool.observer;

import com.drivingschool.model.Appointment;

public interface Observer {
    void update(Appointment appointment, String message);
    String getObserverName();
}
