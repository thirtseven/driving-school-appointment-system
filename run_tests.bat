@echo off
chcp 65001 >nul
echo ========================================
echo   CDP自动化测试启动脚本
echo ========================================
echo.

cd cdp

echo [1/2] 检查依赖...
if not exist "node_modules" (
    echo 首次运行，安装依赖...
    npm install
    if %errorlevel% neq 0 (
        echo 依赖安装失败！请检查Node.js环境。
        pause
        exit /b 1
    )
)
echo 依赖检查完成！

echo.
echo [2/2] 运行CDP测试...
echo.
node run_tests.js

echo.
echo ========================================
echo   测试完成！结果保存在 cdp/results 目录
echo ========================================
echo.
pause
