package com.drivingschool.template;

import com.drivingschool.model.Appointment;
import com.drivingschool.strategy.DiscountContext;
import com.drivingschool.strategy.DiscountStrategy;

public class StandardAppointmentTemplate extends AppointmentTemplate {
    private final DiscountStrategy discountStrategy;

    public StandardAppointmentTemplate(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    @Override
    protected void registerInformation(Appointment appointment) {
        System.out.println("[信息登记] 学员: " + appointment.getStudent().getName());
        System.out.println("[信息登记] 课程: " + appointment.getCourse().getName());
        System.out.println("[信息登记] 练车时间: " + appointment.getFormattedClassTime());
    }

    @Override
    protected double calculateFee(Appointment appointment) {
        double originalPrice = appointment.getCourse().calculatePrice();
        DiscountContext context = new DiscountContext(discountStrategy);
        double finalPrice = context.applyDiscount(originalPrice);

        System.out.println("[费用计算] 原价: " + String.format("%.2f", originalPrice) + "元");
        System.out.println("[费用计算] 优惠策略: " + context.getStrategyName());
        System.out.println("[费用计算] 折后价格: " + String.format("%.2f", finalPrice) + "元");

        return finalPrice;
    }

    @Override
    protected void assignCoach(Appointment appointment) {
        String coachName = assignCoachByCourse(appointment.getCourse().getName());
        System.out.println("[安排教练] 分配教练: " + coachName);
    }

    private String assignCoachByCourse(String courseName) {
        switch (courseName) {
            case "科二基础":
                return "王教练";
            case "科三上路":
                return "李教练";
            case "模拟考试":
                return "张教练";
            default:
                return "待分配";
        }
    }
}
