package com.drivingschool.observer;

import com.drivingschool.model.Appointment;

public class WeChatObserver implements Observer {
    @Override
    public void update(Appointment appointment, String message) {
        System.out.println("[微信通知] 发送至学员: " + appointment.getStudent().getName());
        System.out.println("[微信通知] 内容: " + message);
    }

    @Override
    public String getObserverName() {
        return "微信通知服务";
    }
}
