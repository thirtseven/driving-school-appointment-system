#!/bin/bash

echo "========================================"
echo "  安装驾校预约系统命令别名"
echo "========================================"
echo ""

BASHRC="$HOME/.bashrc"
ALIASES_FILE="/mnt/c/Users/Lenovo/DrivingSchoolSystem/bashrc_aliases.sh"

if grep -q "DRIVING_SCHOOL_HOME" "$BASHRC" 2>/dev/null; then
    echo "✓ 别名已存在于 ~/.bashrc"
else
    echo "添加别名到 ~/.bashrc..."
    cat "$ALIASES_FILE" >> "$BASHRC"
    echo "✓ 别名已添加"
fi

echo ""
echo "========================================"
echo "  安装完成！"
echo "========================================"
echo ""
echo "请执行以下命令使别名生效："
echo ""
echo "  source ~/.bashrc"
echo ""
echo "然后使用 ds-help 查看所有可用命令"
echo ""
