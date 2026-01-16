#!/bin/bash

# 测试数据库错误响应
# 测试重复 email、重复 phone、重复用户名等约束违反

BASE_URL="http://localhost:8080/user"

echo "========================================="
echo "测试数据库约束违反错误响应"
echo "========================================="
echo ""

# 1. 创建第一个用户（应该成功）
echo "1. 创建第一个用户（应该成功）"
echo "-----------------------------------------"
response=$(curl -s -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","email":"test@example.com","phone":"13800138000"}')
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 2. 尝试创建用户名重复的用户（应该失败）
echo "2. 尝试创建用户名重复的用户（应该返回 400 错误）"
echo "-----------------------------------------"
response=$(curl -s -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","email":"another@example.com","phone":"13800138001"}')
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""
echo "✓ 应该看到：{\"code\": 400, \"message\": \"用户名已存在\"}"
echo ""

# 3. 尝试创建 email 重复的用户（应该失败）
echo "3. 尝试创建 email 重复的用户（应该返回 400 错误）"
echo "-----------------------------------------"
response=$(curl -s -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"anotheruser","email":"test@example.com","phone":"13800138002"}')
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""
echo "✓ 应该看到：{\"code\": 400, \"message\": \"邮箱已被使用\"}"
echo ""

# 4. 尝试创建 phone 重复的用户（应该失败）
echo "4. 尝试创建 phone 重复的用户（应该返回 400 错误）"
echo "-----------------------------------------"
response=$(curl -s -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"phoneuser","email":"phone@example.com","phone":"13800138000"}')
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""
echo "✓ 应该看到：{\"code\": 400, \"message\": \"手机号已被使用\"}"
echo ""

# 5. 创建另一个用户用于更新测试
echo "5. 创建第二个用户用于更新测试"
echo "-----------------------------------------"
response=$(curl -s -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"updateuser","email":"update@example.com","phone":"13800138003"}')
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
# 查询用户以获取 ID
user_id=$(curl -s "${BASE_URL}/name/updateuser" | python3 -c "import sys, json; data = json.load(sys.stdin); print(data.get('data', {}).get('id', 0) if isinstance(data.get('data'), dict) else 0)" 2>/dev/null)
echo ""

# 6. 尝试更新用户的 email 为已存在的 email（应该失败）
if [ "$user_id" != "0" ] && [ -n "$user_id" ]; then
    echo "6. 尝试更新用户 email 为已存在的 email（应该返回 400 错误）"
    echo "-----------------------------------------"
    response=$(curl -s -X PUT "${BASE_URL}/${user_id}" \
      -H "Content-Type: application/json" \
      -d '{"email":"test@example.com"}')
    echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
    echo ""
    echo "✓ 应该看到：{\"code\": 400, \"message\": \"邮箱已被使用\"}"
    echo ""
fi

# 7. 清理测试数据
echo "7. 清理测试数据"
echo "-----------------------------------------"
echo "删除测试用户..."
for id in 1 2; do
    curl -s -X DELETE "${BASE_URL}/${id}" > /dev/null
    echo "Deleted user ID: $id"
done
echo ""

echo "========================================="
echo "测试完成"
echo ""
echo "验证要点："
echo "1. ✓ 重复用户名返回 400 错误: '用户名已存在'"
echo "2. ✓ 重复 email 返回 400 错误: '邮箱已被使用'"
echo "3. ✓ 重复 phone 返回 400 错误: '手机号已被使用'"
echo "4. ✓ 更新时违反约束返回 400 错误"
echo "5. ✓ 错误消息友好，易于理解"
echo "========================================="
