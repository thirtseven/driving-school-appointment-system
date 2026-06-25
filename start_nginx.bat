@echo off
chcp 65001 >nul
echo ========================================
echo   Nginx反向代理启动脚本
echo ========================================
echo.

set NGINX_PATH=C:\nginx\nginx.exe

if not exist "%NGINX_PATH%" (
    echo 错误: 未找到Nginx！
    echo 请将Nginx安装到 C:\nginx\ 目录
    echo 或修改此脚本中的NGINX_PATH变量
    pause
    exit /b 1
)

echo [1/2] 复制Nginx配置文件...
copy /Y nginx\nginx.conf C:\nginx\conf\nginx.conf >nul
echo 配置文件已更新！

echo.
echo [2/2] 启动Nginx...
cd /d C:\nginx
start "Nginx" nginx.exe
cd /d %~dp0

echo.
echo ========================================
echo   Nginx已启动！
echo   访问地址: http://localhost
echo   反向代理到: http://localhost:8080
echo ========================================
echo.
pause
