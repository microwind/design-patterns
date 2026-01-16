#!/bin/bash

# User Demo API 测试脚本
# 测试用户管理系统的所有接口

BASE_URL="http://localhost:8080/user"

echo "========== User Demo API 测试脚本 =========="
echo "基础 URL: $BASE_URL"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试函数
test_api() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "${BLUE}=== $description ===${NC}"
    echo "请求: $method $BASE_URL$endpoint"
    
    if [ -z "$data" ]; then
        if [ "$method" = "GET" ]; then
            curl -s -X GET "$BASE_URL$endpoint" -H "Content-Type: application/json" | python3 -m json.tool
        fi
    else
        echo "数据: $data"
        curl -s -X POST "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data" | python3 -m json.tool
    fi
    echo ""
}

# 测试 1: 获取用户列表
test_api "GET" "/list" "" "测试 1: 获取用户列表"

# 测试 2: 获取激活的用户
test_api "GET" "/active" "" "测试 2: 获取激活的用户"

# 测试 3: 获取用户总数
test_api "GET" "/count" "" "测试 3: 获取用户总数"

# 测试 4: 根据 ID 获取用户
test_api "GET" "/get?id=1" "" "测试 4: 根据 ID 获取用户 (ID=1)"

# 测试 5: 根据用户名获取用户
test_api "GET" "/getByUsername?username=admin" "" "测试 5: 根据用户名获取用户 (username=admin)"

# 测试 6: 用户登录 - 正确的凭证
test_api "POST" "/login" '{"username":"admin","password":"123456"}' "测试 6: 用户登录 (admin/123456)"

# 测试 7: 用户登录 - 错误的密码
test_api "POST" "/login" '{"username":"admin","password":"wrongpassword"}' "测试 7: 用户登录 - 错误密码"

# 测试 8: 创建新用户
test_api "POST" "/create" '{"username":"testuser","password":"test123","email":"testuser@example.com","phone":"13800138888"}' "测试 8: 创建新用户"

# 测试 9: 创建重复用户名 (应该失败)
test_api "POST" "/create" '{"username":"admin","password":"password123","email":"admin2@example.com","phone":"13800138111"}' "测试 9: 创建重复用户名 (应该失败)"

# 测试 10: 更新用户
test_api "POST" "/update" '{"id":1,"username":"admin","password":"newpassword123","email":"admin@newemail.com","phone":"13800138000","status":1}' "测试 10: 更新用户"

# 测试 11: 删除用户 (删除 testuser)
test_api "POST" "/delete?id=4" "" "测试 11: 删除用户 (ID=4)"

echo -e "${GREEN}========== 所有测试完成 ==========${NC}"
echo ""
echo "测试说明:"
echo "1. 确保应用已启动，Web 服务器运行在 http://localhost:8080"
echo "2. 数据库已创建并初始化 user 表"
echo "3. 如果看到 JSON 响应，说明 API 调用成功"
echo "4. 如果 curl 命令不可用，请使用其他 HTTP 客户端如 Postman"
