# 驾校学车预约管理系统

基于Java设计模式的驾校预约管理系统，包含Web前端管理界面、REST API服务、Nginx反向代理和CDP自动化测试。

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Web Frontend                           │
│                    (HTML/CSS/JavaScript)                     │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nginx Reverse Proxy                       │
│                    (Port 80 → 8080)                         │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Java HTTP Server                           │
│                    (Port 8080)                               │
├─────────────────────────────────────────────────────────────┤
│  REST API Endpoints:                                        │
│  - /api/appointments  (预约管理)                            │
│  - /api/courses       (课程管理)                            │
│  - /api/students      (学员管理)                            │
│  - /api/statistics    (统计信息)                            │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  Design Patterns Layer                       │
├─────────────────────────────────────────────────────────────┤
│  Singleton    │ Factory      │ Decorator                    │
│  Strategy     │ Template     │ Observer                     │
└─────────────────────────────────────────────────────────────┘
```

## 快速开始

### 方式一：直接启动Java服务器

```bash
# 编译项目
javac -d bin -sourcepath src/main/java src/main/java/com/drivingschool/**/*.java

# 启动服务器
java -cp bin com.drivingschool.server.HttpServerApp
```

访问 http://localhost:8080

### 方式二：使用Windows批处理脚本

```bash
# 双击运行
start_server.bat
```

### 方式三：使用Nginx反向代理

1. 将Nginx安装到 `C:\nginx\`
2. 运行 `start_nginx.bat`
3. 访问 http://localhost

## 项目结构

```
DrivingSchoolSystem/
├── src/main/java/com/drivingschool/
│   ├── model/                    # 数据模型
│   │   ├── Course.java
│   │   ├── SubjectTwoCourse.java
│   │   ├── SubjectThreeCourse.java
│   │   ├── MockExamCourse.java
│   │   ├── Student.java
│   │   └── Appointment.java
│   ├── singleton/               # 单例模式
│   │   └── AppointmentManager.java
│   ├── factory/                 # 工厂方法模式
│   │   ├── CourseFactory.java
│   │   ├── SubjectTwoFactory.java
│   │   ├── SubjectThreeFactory.java
│   │   └── MockExamFactory.java
│   ├── decorator/               # 装饰器模式
│   │   ├── CourseDecorator.java
│   │   ├── ExamVenueSimulation.java
│   │   └── SprintCoaching.java
│   ├── strategy/                # 策略模式
│   │   ├── DiscountStrategy.java
│   │   ├── RegularDiscountStrategy.java
│   │   ├── StudentDiscountStrategy.java
│   │   ├── GroupDiscountStrategy.java
│   │   └── DiscountContext.java
│   ├── template/                # 模板方法模式
│   │   ├── AppointmentTemplate.java
│   │   └── StandardAppointmentTemplate.java
│   ├── observer/                # 观察者模式
│   │   ├── Observer.java
│   │   ├── Subject.java
│   │   ├── SmsObserver.java
│   │   └── WeChatObserver.java
│   ├── server/                  # HTTP服务器
│   │   ├── HttpServerConfig.java
│   │   ├── HttpServerApp.java
│   │   ├── AppointmentHandler.java
│   │   ├── CourseHandler.java
│   │   ├── StudentHandler.java
│   │   ├── StatisticsHandler.java
│   │   └── StaticFileHandler.java
│   └── Main.java                # 控制台演示程序
├── web/                         # Web前端
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       └── app.js
├── nginx/                       # Nginx配置
│   └── nginx.conf
├── cdp/                         # CDP自动化测试
│   ├── run_tests.js
│   ├── package.json
│   └── results/                 # 测试结果
├── start_server.bat             # 服务器启动脚本
├── start_nginx.bat              # Nginx启动脚本
├── run_tests.bat                # 测试运行脚本
└── README.md
```

## 设计模式说明

### 1. 单例模式 (Singleton)
- **类**: `AppointmentManager`
- **用途**: 全局唯一的预约管理中心
- **实现**: 双重检查锁定 + volatile

### 2. 工厂方法模式 (Factory Method)
- **接口**: `CourseFactory`
- **实现**: `SubjectTwoFactory`, `SubjectThreeFactory`, `MockExamFactory`
- **用途**: 创建不同类型的练车课程

### 3. 装饰器模式 (Decorator)
- **基类**: `CourseDecorator`
- **实现**: `ExamVenueSimulation`, `SprintCoaching`
- **用途**: 动态叠加增值服务

### 4. 策略模式 (Strategy)
- **接口**: `DiscountStrategy`
- **实现**: `RegularDiscountStrategy`, `StudentDiscountStrategy`, `GroupDiscountStrategy`
- **用途**: 不同学员类型的优惠计算

### 5. 模板方法模式 (Template Method)
- **基类**: `AppointmentTemplate`
- **实现**: `StandardAppointmentTemplate`
- **用途**: 固定预约流程（信息登记→费用计算→安排教练）

### 6. 观察者模式 (Observer)
- **接口**: `Subject`, `Observer`
- **实现**: `Appointment`, `SmsObserver`, `WeChatObserver`
- **用途**: 预约状态变更通知

## Web前端功能

### 仪表盘
- 总预约数统计
- 总收入统计
- 课程预约分布图表
- 设计模式应用概览

### 预约管理
- 查看所有预约记录
- 预约状态显示
- 新增预约（开发中）

### 课程管理
- 科目二基础课程
- 科目三上路课程
- 模拟考试课程
- 增值服务展示

### 学员管理
- 学员列表
- 学员类型显示
- 优惠策略显示

### 自动化测试
- 页面加载测试
- API接口测试
- UI交互测试
- 完整测试套件

## CDP自动化测试

### 安装依赖

```bash
cd cdp
npm install
```

### 运行测试

```bash
# 运行所有测试
npm test

# 详细模式
npm run test:verbose
```

### 测试内容

1. **页面加载测试**: 验证页面正常加载
2. **导航测试**: 验证各区块切换正常
3. **API测试**: 验证所有API端点响应正常
4. **UI测试**: 验证界面元素存在且正确

### 测试结果

测试结果保存在 `cdp/results/` 目录，格式为JSON。

## Nginx配置

### 配置说明

```nginx
upstream driving_school_backend {
    server 127.0.0.1:8080;  # 后端服务器地址
}

server {
    listen 80;               # 监听端口
    server_name localhost;   # 服务器名称

    location / {
        proxy_pass http://driving_school_backend;  # 反向代理
    }
}
```

### 启动Nginx

1. 下载Nginx: http://nginx.org/
2. 解压到 `C:\nginx\`
3. 运行 `start_nginx.bat`

## API文档

### 预约API

```
GET /api/appointments
Response: {
    "appointments": [...],
    "count": 0
}
```

### 课程API

```
GET /api/courses
Response: {
    "courses": [
        {
            "name": "科二基础",
            "basePrice": 2800.00,
            "duration": 12,
            "description": "..."
        },
        ...
    ]
}
```

### 学员API

```
GET /api/students
Response: {
    "students": [...],
    "count": 4
}
```

### 统计API

```
GET /api/statistics
Response: {
    "totalAppointments": 0,
    "totalRevenue": 0.00,
    "courseStats": {...},
    "statusStats": {...}
}
```

## 环境要求

- Java 8+
- Node.js 14+ (用于CDP测试)
- Nginx (可选，用于反向代理)
- 现代浏览器 (Chrome, Firefox, Edge)

## 常见问题

### 1. 端口被占用

```bash
# 查看端口占用
netstat -ano | findstr :8080

# 终止进程
taskkill /PID <进程ID> /F
```

### 2. Nginx启动失败

检查配置文件语法：
```bash
C:\nginx\nginx.exe -t
```

### 3. CDP测试失败

确保：
1. Java服务器正在运行
2. Node.js已安装
3. Puppeteer依赖已安装

## 许可证

MIT License
