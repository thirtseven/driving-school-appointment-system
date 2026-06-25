@echo off
chcp 65001 >nul
echo ========================================
echo   驾校预约系统启动脚本
echo ========================================
echo.

echo [1/3] 编译Java项目...
if not exist "bin" mkdir bin
javac -d bin -sourcepath src/main/java src/main/java/com/drivingschool/**/*.java src/main/java/com/drivingschool/*.java 2>nul
if %errorlevel% neq 0 (
    echo 编译失败！请检查Java环境。
    pause
    exit /b 1
)
echo 编译成功！

echo.
echo [2/3] 启动Java HTTP服务器...
start "Driving School Server" java -cp bin com.drivingschool.server.HttpServerApp
echo 服务器启动中...

echo.
echo [3/3] 等待服务器就绪...
timeout /t 3 /nobreak >nul

echo.
echo ========================================
echo   系统已启动！
echo   访问地址: http://localhost:8080
echo ========================================
echo.
echo 按任意键打开浏览器...
pause >nul
start http://localhost:8080
