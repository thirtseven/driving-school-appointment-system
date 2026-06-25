package com.drivingschool.observer;

import com.drivingschool.model.Appointment;

public class SmsObserver implements Observer {
    @Override
    public void update(Appointment appointment, String message) {
        System.out.println("[短信通知] 发送至: " + appointment.getStudent().getPhone());
        System.out.println("[短信通知] 内容: " + message);
    }

    @Override
    public String getObserverName() {
        return "短信通知服务";
    }
}
