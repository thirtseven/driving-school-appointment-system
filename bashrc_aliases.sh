# Driving School Appointment System - Bash Aliases
# Add this to ~/.bashrc or source it directly

# Project root directory
export DRIVING_SCHOOL_HOME="/mnt/c/Users/Lenovo/DrivingSchoolSystem"

# Java path (Windows JDK via WSL)
export JAVA_HOME="/mnt/c/Java/jdk-21.0.2+13"
export JAVA_CMD="$JAVA_HOME/bin/java.exe"
export JAVAC_CMD="$JAVA_HOME/bin/javac.exe"

# ==================== Navigation ====================

alias ds='cd $DRIVING_SCHOOL_HOME'
alias ds-src='cd $DRIVING_SCHOOL_HOME/src/main/java/com/drivingschool'
alias ds-web='cd $DRIVING_SCHOOL_SYSTEM/web'
alias ds-cdp='cd $DRIVING_SCHOOL_HOME/cdp'
alias ds-nginx='cd $DRIVING_SCHOOL_HOME/nginx'

# ==================== Build ====================

ds-compile() {
    echo "========================================"
    echo "  编译驾校预约系统"
    echo "========================================"
    cd "$DRIVING_SCHOOL_HOME"
    rm -rf bin
    mkdir -p bin
    $JAVAC_CMD -d bin -sourcepath src/main/java \
        src/main/java/com/drivingschool/**/*.java \
        src/main/java/com/drivingschool/*.java 2>&1
    
    if [ $? -eq 0 ]; then
        echo "✓ 编译成功！"
    else
        echo "✗ 编译失败！"
        return 1
    fi
}

# ==================== Server ====================

ds-start() {
    local port=${1:-8080}
    echo "========================================"
    echo "  启动驾校预约系统服务器 (端口: $port)"
    echo "========================================"
    cd "$DRIVING_SCHOOL_HOME"
    
    if [ ! -d "bin" ]; then
        echo "首次运行，先编译项目..."
        ds-compile
    fi
    
    echo "启动服务器..."
    $JAVA_CMD -cp bin com.drivingschool.server.HttpServerApp $port &
    echo $! > /tmp/ds_server.pid
    echo "✓ 服务器已启动 (PID: $(cat /tmp/ds_server.pid))"
    echo "  访问地址: http://localhost:$port"
}

ds-stop() {
    if [ -f /tmp/ds_server.pid ]; then
        local pid=$(cat /tmp/ds_server.pid)
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            rm /tmp/ds_server.pid
            echo "✓ 服务器已停止 (PID: $pid)"
        else
            echo "服务器未运行"
            rm /tmp/ds_server.pid
        fi
    else
        echo "未找到服务器进程ID文件"
        # Try to find and kill by port
        local pid=$(lsof -ti:8080 2>/dev/null)
        if [ -n "$pid" ]; then
            kill $pid
            echo "✓ 已停止占用8080端口的进程 (PID: $pid)"
        fi
    fi
}

ds-restart() {
    ds-stop
    sleep 1
    ds-start ${1:-8080}
}

ds-status() {
    echo "========================================"
    echo "  驾校预约系统状态"
    echo "========================================"
    
    if [ -f /tmp/ds_server.pid ]; then
        local pid=$(cat /tmp/ds_server.pid)
        if kill -0 $pid 2>/dev/null; then
            echo "✓ 服务器运行中 (PID: $pid)"
            echo "  访问地址: http://localhost:8080"
            
            # Test API
            local response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/courses 2>/dev/null)
            if [ "$response" = "200" ]; then
                echo "✓ API响应正常"
            else
                echo "✗ API响应异常 (HTTP $response)"
            fi
        else
            echo "✗ 服务器未运行 (PID文件存在但进程不存在)"
        fi
    else
        echo "✗ 服务器未运行"
    fi
}

# ==================== API Testing ====================

ds-api() {
    local endpoint=${1:-courses}
    echo "调用 API: /api/$endpoint"
    curl -s "http://localhost:8080/api/$endpoint" | python3 -m json.tool 2>/dev/null || \
    curl -s "http://localhost:8080/api/$endpoint"
}

ds-api-all() {
    echo "========================================"
    echo "  测试所有API端点"
    echo "========================================"
    
    for endpoint in appointments courses students statistics; do
        echo ""
        echo "--- /api/$endpoint ---"
        local response=$(curl -s -w "\n%{http_code}" "http://localhost:8080/api/$endpoint" 2>/dev/null)
        local status=$(echo "$response" | tail -1)
        local body=$(echo "$response" | sed '$d')
        
        if [ "$status" = "200" ]; then
            echo "✓ HTTP $status"
            echo "$body" | head -c 200
            echo "..."
        else
            echo "✗ HTTP $status"
        fi
    done
}

# ==================== Web Frontend ====================

ds-web-start() {
    echo "在浏览器中打开驾校预约系统..."
    cmd.exe /c start http://localhost:8080 2>/dev/null || \
    echo "请手动打开: http://localhost:8080"
}

# ==================== Nginx ====================

ds-nginx-start() {
    echo "========================================"
    echo "  启动Nginx反向代理"
    echo "========================================"
    
    # Copy nginx config
    if [ -f "$DRIVING_SCHOOL_HOME/nginx/nginx.conf" ]; then
        cp "$DRIVING_SCHOOL_HOME/nginx/nginx.conf" /mnt/c/nginx/conf/nginx.conf 2>/dev/null
        echo "✓ 配置文件已更新"
    fi
    
    # Start nginx
    cmd.exe /c "cd /d C:\\nginx && start nginx.exe" 2>/dev/null
    echo "✓ Nginx已启动"
    echo "  访问地址: http://localhost"
}

ds-nginx-stop() {
    cmd.exe /c "taskkill /IM nginx.exe /F" 2>/dev/null
    echo "✓ Nginx已停止"
}

ds-nginx-restart() {
    ds-nginx-stop
    sleep 1
    ds-nginx-start
}

# ==================== CDP Testing ====================

ds-test() {
    echo "========================================"
    echo "  运行CDP自动化测试"
    echo "========================================"
    cd "$DRIVING_SCHOOL_HOME/cdp"
    
    if [ ! -d "node_modules" ]; then
        echo "首次运行，安装依赖..."
        npm install
    fi
    
    node run_tests.js
}

ds-test-install() {
    echo "安装CDP测试依赖..."
    cd "$DRIVING_SCHOOL_HOME/cdp"
    npm install
}

# ==================== Console Demo ====================

ds-demo() {
    echo "========================================"
    echo "  运行控制台演示程序"
    echo "========================================"
    cd "$DRIVING_SCHOOL_HOME"
    
    if [ ! -d "bin" ]; then
        ds-compile
    fi
    
    $JAVA_CMD -cp bin com.drivingschool.Main
}

# ==================== Logs ====================

ds-logs() {
    if [ -f "$DRIVING_SCHOOL_HOME/logs/server.log" ]; then
        tail -f "$DRIVING_SCHOOL_HOME/logs/server.log"
    else
        echo "日志文件不存在"
    fi
}

# ==================== Clean ====================

ds-clean() {
    echo "清理编译文件..."
    cd "$DRIVING_SCHOOL_HOME"
    rm -rf bin
    rm -f /tmp/ds_server.pid
    echo "✓ 清理完成"
}

# ==================== Help ====================

ds-help() {
    echo "========================================"
    echo "  驾校预约系统 - 命令列表"
    echo "========================================"
    echo ""
    echo "导航命令:"
    echo "  ds              - 进入项目根目录"
    echo "  ds-src          - 进入源代码目录"
    echo "  ds-web          - 进入Web前端目录"
    echo "  ds-cdp          - 进入CDP测试目录"
    echo "  ds-nginx        - 进入Nginx配置目录"
    echo ""
    echo "构建命令:"
    echo "  ds-compile      - 编译Java项目"
    echo "  ds-clean        - 清理编译文件"
    echo ""
    echo "服务器命令:"
    echo "  ds-start [port] - 启动服务器 (默认端口8080)"
    echo "  ds-stop         - 停止服务器"
    echo "  ds-restart      - 重启服务器"
    echo "  ds-status       - 查看服务器状态"
    echo ""
    echo "API测试:"
    echo "  ds-api [endpoint] - 调用API端点"
    echo "  ds-api-all        - 测试所有API端点"
    echo ""
    echo "Web前端:"
    echo "  ds-web-start    - 在浏览器中打开"
    echo ""
    echo "Nginx命令:"
    echo "  ds-nginx-start   - 启动Nginx"
    echo "  ds-nginx-stop    - 停止Nginx"
    echo "  ds-nginx-restart - 重启Nginx"
    echo ""
    echo "测试命令:"
    echo "  ds-test         - 运行CDP自动化测试"
    echo "  ds-test-install - 安装测试依赖"
    echo ""
    echo "其他命令:"
    echo "  ds-demo         - 运行控制台演示"
    echo "  ds-logs         - 查看服务器日志"
    echo "  ds-help         - 显示此帮助信息"
    echo ""
    echo "========================================"
}
