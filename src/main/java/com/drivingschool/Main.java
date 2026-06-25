package com.drivingschool;

import com.drivingschool.decorator.ExamVenueSimulation;
import com.drivingschool.decorator.SprintCoaching;
import com.drivingschool.factory.CourseFactory;
import com.drivingschool.factory.SubjectThreeFactory;
import com.drivingschool.model.Appointment;
import com.drivingschool.model.Course;
import com.drivingschool.model.Student;
import com.drivingschool.model.Student.StudentType;
import com.drivingschool.observer.SmsObserver;
import com.drivingschool.observer.WeChatObserver;
import com.drivingschool.singleton.AppointmentManager;
import com.drivingschool.strategy.DiscountStrategy;
import com.drivingschool.strategy.DiscountContext;
import com.drivingschool.template.AppointmentTemplate;
import com.drivingschool.template.StandardAppointmentTemplate;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      驾校学车预约管理系统 - 完整流程演示");
        System.out.println("==========================================\n");

        // Step 1: 单例模式 - 获取预约管理中心
        System.out.println("【步骤1】单例模式 - 获取预约管理中心");
        AppointmentManager manager = AppointmentManager.getInstance();
        System.out.println("预约管理中心初始化完成，当前预约数量: " + manager.getAppointmentCount());

        // Step 2: 工厂方法模式 - 创建科三课程
        System.out.println("\n【步骤2】工厂方法模式 - 创建科三上路课程");
        CourseFactory courseFactory = new SubjectThreeFactory();
        Course subjectThree = courseFactory.createCourse();
        System.out.println("课程创建成功: " + subjectThree.getName());
        System.out.println("课程详情: " + subjectThree.getDescription());
        System.out.println("基础价格: " + String.format("%.2f", subjectThree.getBasePrice()) + "元");

        // Step 3: 装饰器模式 - 叠加增值服务
        System.out.println("\n【步骤3】装饰器模式 - 叠加增值服务");
        Course courseWithSimulation = new ExamVenueSimulation(subjectThree);
        System.out.println("添加考场模拟服务: " + courseWithSimulation.getName());
        System.out.println("当前价格: " + String.format("%.2f", courseWithSimulation.calculatePrice()) + "元");

        Course courseWithBothServices = new SprintCoaching(courseWithSimulation);
        System.out.println("添加考前冲刺辅导: " + courseWithBothServices.getName());
        System.out.println("最终价格: " + String.format("%.2f", courseWithBothServices.calculatePrice()) + "元");
        System.out.println("完整服务: " + courseWithBothServices.getDescription());

        // Step 4: 策略模式 - 选择学生优惠
        System.out.println("\n【步骤4】策略模式 - 选择学生优惠");
        Student student = new Student("STU001", "张三", "13800138000", StudentType.STUDENT);
        System.out.println("学员信息: " + student);

        DiscountStrategy discountStrategy = DiscountContext.createStrategyByStudentType(student.getType());
        System.out.println("优惠策略: " + discountStrategy.getStrategyName());

        double originalPrice = courseWithBothServices.calculatePrice();
        double discountedPrice = discountStrategy.calculateDiscount(originalPrice);
        System.out.println("原价: " + String.format("%.2f", originalPrice) + "元");
        System.out.println("折后价: " + String.format("%.2f", discountedPrice) + "元");
        System.out.println("节省: " + String.format("%.2f", originalPrice - discountedPrice) + "元");

        // Step 5: 模板方法模式 - 执行预约流程
        System.out.println("\n【步骤5】模板方法模式 - 执行预约流程");
        LocalDateTime classTime = LocalDateTime.now().plusDays(3).withHour(9).withMinute(0);
        AppointmentTemplate template = new StandardAppointmentTemplate(discountStrategy);
        Appointment appointment = template.executeAppointment(student, courseWithBothServices, classTime);

        // Step 6: 观察者模式 - 注册通知渠道
        System.out.println("【步骤6】观察者模式 - 注册通知渠道");
        appointment.registerObserver(new SmsObserver());
        appointment.registerObserver(new WeChatObserver());

        // Step 7: 保存预约并触发通知
        System.out.println("\n【步骤7】保存预约并触发通知");
        manager.addAppointment(appointment);

        // 模拟练车前通知
        String reminderMessage = String.format("尊敬的%s学员，您预约的%s课程将于%s开始，请准时到达训练场。",
                student.getName(),
                courseWithBothServices.getName(),
                appointment.getFormattedClassTime());
        appointment.notifyObservers(reminderMessage);

        // 显示所有预约记录
        manager.printAllAppointments();

        // 设计模式总结
        printDesignPatternSummary();

        System.out.println("\n==========================================");
        System.out.println("          系统演示完成！");
        System.out.println("==========================================");
    }

    private static void printDesignPatternSummary() {
        System.out.println("\n========== 设计模式应用总结 ==========");
        System.out.println("1. 单例模式: AppointmentManager - 全局唯一预约管理中心");
        System.out.println("2. 工厂方法模式: CourseFactory - 创建不同类型的练车课程");
        System.out.println("3. 装饰器模式: CourseDecorator - 叠加考场模拟、冲刺辅导增值服务");
        System.out.println("4. 策略模式: DiscountStrategy - 普通原价、学生8折、团体7折优惠");
        System.out.println("5. 模板方法模式: AppointmentTemplate - 固定预约流程(信息登记→费用计算→安排教练)");
        System.out.println("6. 观察者模式: Subject/Observer - 短信、微信推送练车提醒");
        System.out.println("=====================================\n");
    }
}
