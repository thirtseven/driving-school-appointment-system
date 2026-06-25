# 驾校学车预约管理系统 - UML类图

## 系统架构概述

本系统采用6种设计模式实现驾校学车预约管理功能，代码结构清晰分层。

## 设计模式对应类图

### 1. 单例模式 (Singleton Pattern)

```
┌─────────────────────────────────────┐
│      AppointmentManager             │
├─────────────────────────────────────┤
│ - instance: AppointmentManager      │
│ - appointments: List<Appointment>   │
├─────────────────────────────────────┤
│ + getInstance(): AppointmentManager │
│ + addAppointment(Appointment): void │
│ + findAppointment(String): Optional │
│ + getAllAppointments(): List         │
│ + getAppointmentCount(): int        │
│ + printAllAppointments(): void      │
└─────────────────────────────────────┘
```

### 2. 工厂方法模式 (Factory Method Pattern)

```
┌─────────────────────────┐
│   «interface»           │
│    CourseFactory         │
├─────────────────────────┤
│ + createCourse(): Course │
└─────────────────────────┘
           △
           │
    ┌──────┴──────┬─────────────────┐
    │             │                 │
┌───┴───┐  ┌─────┴─────┐  ┌───────┴───────┐
│Subject│  │Subject    │  │MockExam       │
│Two    │  │Three      │  │Factory        │
│Factory│  │Factory    │  │               │
└───────┘  └───────────┘  └───────────────┘
    │             │                 │
    ▼             ▼                 ▼
┌─────────────────────────────────────────────┐
│           «abstract» Course                  │
├─────────────────────────────────────────────┤
│ # name: String                              │
│ # basePrice: double                         │
│ # duration: int                             │
├─────────────────────────────────────────────┤
│ + getName(): String                         │
│ + getBasePrice(): double                    │
│ + getDuration(): int                        │
│ + «abstract» calculatePrice(): double       │
│ + «abstract» getDescription(): String       │
└─────────────────────────────────────────────┘
           △
    ┌──────┴──────┬─────────────────┐
    │             │                 │
┌───┴───┐  ┌─────┴─────┐  ┌───────┴───────┐
│Subject│  │Subject    │  │MockExam       │
│Two    │  │Three      │  │Course         │
│Course │  │Course     │  │               │
└───────┘  └───────────┘  └───────────────┘
```

### 3. 装饰器模式 (Decorator Pattern)

```
┌─────────────────────────────────────────────┐
│           «abstract» Course                  │
├─────────────────────────────────────────────┤
│ # name: String                              │
│ # basePrice: double                         │
│ # duration: int                             │
├─────────────────────────────────────────────┤
│ + «abstract» calculatePrice(): double       │
│ + «abstract» getDescription(): String       │
└─────────────────────────────────────────────┘
           △
           │
    ┌──────┴──────┐
    │             │
┌───┴───┐  ┌─────┴───────────────────────────┐
│Course │  │«abstract» CourseDecorator        │
│(base) │  ├─────────────────────────────────┤
└───────┘  │ # decoratedCourse: Course       │
           ├─────────────────────────────────┤
           │ + getDuration(): int            │
           └─────────────────────────────────┘
                       △
              ┌────────┴────────┐
              │                 │
    ┌─────────┴─────┐  ┌───────┴───────────┐
    │ExamVenue      │  │SprintCoaching     │
    │Simulation     │  │                   │
    ├───────────────┤  ├───────────────────┤
    │+ calculate    │  │+ calculate        │
    │  Price():     │  │  Price():         │
    │  double       │  │  double           │
    │+ getDescrip   │  │+ getDescrip       │
    │  tion():      │  │  tion():          │
    │  String       │  │  String           │
    └───────────────┘  └───────────────────┘
```

### 4. 策略模式 (Strategy Pattern)

```
┌─────────────────────────────────────────────┐
│           «interface» DiscountStrategy       │
├─────────────────────────────────────────────┤
│ + calculateDiscount(double): double         │
│ + getStrategyName(): String                 │
└─────────────────────────────────────────────┘
           △
    ┌──────┴──────┬─────────────────┐
    │             │                 │
┌───┴───────┐ ┌──┴────────┐ ┌─────┴─────────┐
│Regular    │ │Student    │ │Group          │
│Discount   │ │Discount   │ │Discount       │
│Strategy   │ │Strategy   │ │Strategy       │
├───────────┤ ├───────────┤ ├───────────────┤
│DISCOUNT:  │ │DISCOUNT:  │ │DISCOUNT:      │
│1.0        │ │0.8        │ │0.7            │
└───────────┘ └───────────┘ └───────────────┘

┌─────────────────────────────────────────────┐
│           DiscountContext                    │
├─────────────────────────────────────────────┤
│ - strategy: DiscountStrategy                │
├─────────────────────────────────────────────┤
│ + setStrategy(DiscountStrategy): void       │
│ + applyDiscount(double): double             │
│ + getStrategyName(): String                 │
│ + «static» createStrategyByStudentType      │
│   (StudentType): DiscountStrategy           │
└─────────────────────────────────────────────┘
```

### 5. 模板方法模式 (Template Method Pattern)

```
┌─────────────────────────────────────────────┐
│           «abstract» AppointmentTemplate     │
├─────────────────────────────────────────────┤
│                                             │
├─────────────────────────────────────────────┤
│ + «final» executeAppointment                │
│   (Student, Course, LocalDateTime):         │
│   Appointment                               │
│ # «abstract» registerInformation            │
│   (Appointment): void                       │
│ # «abstract» calculateFee                   │
│   (Appointment): double                     │
│ # «abstract» assignCoach                    │
│   (Appointment): void                       │
│ # generateAppointmentId(): String           │
└─────────────────────────────────────────────┘
           △
           │
    ┌──────┴───────────────────────────────┐
    │                                       │
┌───┴───────────────────────────────────────┐
│StandardAppointmentTemplate                │
├───────────────────────────────────────────┤
│ - discountStrategy: DiscountStrategy      │
├───────────────────────────────────────────┤
│ + registerInformation(Appointment): void  │
│ + calculateFee(Appointment): double       │
│ + assignCoach(Appointment): void          │
│ - assignCoachByCourse(String): String     │
└───────────────────────────────────────────┘
```

### 6. 观察者模式 (Observer Pattern)

```
┌─────────────────────────────────────────────┐
│           «interface» Subject                │
├─────────────────────────────────────────────┤
│ + registerObserver(Observer): void          │
│ + removeObserver(Observer): void            │
│ + notifyObservers(String): void             │
└─────────────────────────────────────────────┘
           △
           │
    ┌──────┴──────────────────────────────────┐
    │                                          │
┌───┴──────────────────────────────────────────┐
│Appointment                                   │
├──────────────────────────────────────────────┤
│ - id: String                                │
│ - student: Student                          │
│ - course: Course                            │
│ - appointmentTime: LocalDateTime            │
│ - classTime: LocalDateTime                  │
│ - finalPrice: double                        │
│ - status: AppointmentStatus                 │
│ - observers: List<Observer>                 │
├──────────────────────────────────────────────┤
│ + registerObserver(Observer): void          │
│ + removeObserver(Observer): void            │
│ + notifyObservers(String): void             │
└──────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│           «interface» Observer               │
├─────────────────────────────────────────────┤
│ + update(Appointment, String): void         │
│ + getObserverName(): String                 │
└─────────────────────────────────────────────┘
           △
    ┌──────┴──────┐
    │             │
┌───┴───────┐ ┌──┴─────────────┐
│SmsObserver│ │WeChatObserver  │
├───────────┤ ├────────────────┤
│+ update() │ │+ update()      │
│+ getName()│ │+ getName()     │
└───────────┘ └────────────────┘
```

## 模型类

### Student

```
┌─────────────────────────────────────────────┐
│Student                                       │
├─────────────────────────────────────────────┤
│ - id: String                                │
│ - name: String                              │
│ - phone: String                             │
│ - type: StudentType                         │
├─────────────────────────────────────────────┤
│ + «enum» StudentType {REGULAR, STUDENT,     │
│   GROUP}                                    │
│ + getId(): String                           │
│ + getName(): String                         │
│ + getPhone(): String                        │
│ + getType(): StudentType                    │
└─────────────────────────────────────────────┘
```

### AppointmentStatus

```
┌─────────────────────────────────────────────┐
│«enum» AppointmentStatus                     │
├─────────────────────────────────────────────┤
│ PENDING                                     │
│ CONFIRMED                                   │
│ COMPLETED                                   │
│ CANCELLED                                   │
└─────────────────────────────────────────────┘
```

## 包结构

```
com.drivingschool
├── model/
│   ├── Course.java (abstract)
│   ├── SubjectTwoCourse.java
│   ├── SubjectThreeCourse.java
│   ├── MockExamCourse.java
│   ├── Student.java
│   └── Appointment.java
├── singleton/
│   └── AppointmentManager.java
├── factory/
│   ├── CourseFactory.java (interface)
│   ├── SubjectTwoFactory.java
│   ├── SubjectThreeFactory.java
│   └── MockExamFactory.java
├── decorator/
│   ├── CourseDecorator.java (abstract)
│   ├── ExamVenueSimulation.java
│   └── SprintCoaching.java
├── strategy/
│   ├── DiscountStrategy.java (interface)
│   ├── RegularDiscountStrategy.java
│   ├── StudentDiscountStrategy.java
│   ├── GroupDiscountStrategy.java
│   └── DiscountContext.java
├── template/
│   ├── AppointmentTemplate.java (abstract)
│   └── StandardAppointmentTemplate.java
├── observer/
│   ├── Observer.java (interface)
│   ├── Subject.java (interface)
│   ├── SmsObserver.java
│   └── WeChatObserver.java
└── Main.java
```

## 类关系说明

1. **依赖关系**: Main → 各个设计模式类
2. **实现关系**: 具体类 → 接口/抽象类
3. **组合关系**: Appointment → Student, Course, Observer
4. **聚合关系**: AppointmentManager → Appointment
5. **继承关系**: CourseDecorator → Course
6. **关联关系**: DiscountContext → DiscountStrategy
