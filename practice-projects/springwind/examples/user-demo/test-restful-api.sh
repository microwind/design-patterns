#!/bin/bash

# RESTful 风格的用户 API 测试脚本
# 使用新的 @GetMapping, @PostMapping, @PutMapping, @DeleteMapping 和路径参数

BASE_URL="http://localhost:8080/user"

echo "========================================="
echo "RESTful 用户 API 测试"
echo "========================================="
echo ""

# 1. 创建测试用户
echo "1. 创建用户（POST /user）"
echo "-----------------------------------------"
curl -X POST "${BASE_URL}" \
  -H "Content-Type: application/json" \
  -d '{"name":"testuser","password":"123456","email":"test@example.com","phone":"13800138001"}' \
  -w "\n状态码: %{http_code}\n"
echo ""

# 2. 获取用户列表
echo "2. 获取所有用户（GET /user）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}" -w "\n状态码: %{http_code}\n"
echo ""

# 3. 分页获取用户列表
echo "3. 分页获取用户（GET /user?page=1&pageSize=10）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}?page=1&pageSize=10" -w "\n状态码: %{http_code}\n"
echo ""

# 4. 根据 ID 获取用户（使用路径参数）
echo "4. 根据 ID 获取用户（GET /user/1）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/1" -w "\n状态码: %{http_code}\n"
echo ""

# 5. 根据用户名获取用户（使用路径参数）
echo "5. 根据用户名获取用户（GET /user/name/testuser）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/name/testuser" -w "\n状态码: %{http_code}\n"
echo ""

# 6. 获取用户总数
echo "6. 获取用户总数（GET /user/count）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/count" -w "\n状态码: %{http_code}\n"
echo ""

# 7. 更新用户（使用路径参数）
echo "7. 更新用户（PUT /user/1）"
echo "-----------------------------------------"
curl -X PUT "${BASE_URL}/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"updateduser","email":"updated@example.com","phone":"13800138002"}' \
  -w "\n状态码: %{http_code}\n"
echo ""

# 8. 再次获取用户验证更新
echo "8. 验证更新（GET /user/1）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/1" -w "\n状态码: %{http_code}\n"
echo ""

# 9. 用户登录
echo "9. 用户登录（POST /user/login）"
echo "-----------------------------------------"
curl -X POST "${BASE_URL}/login" \
  -H "Content-Type: application/json" \
  -d '{"name":"updateduser","password":"123456"}' \
  -w "\n状态码: %{http_code}\n"
echo ""

# 10. 删除用户（使用路径参数）
echo "10. 删除用户（DELETE /user/1）"
echo "-----------------------------------------"
curl -X DELETE "${BASE_URL}/1" -w "\n状态码: %{http_code}\n"
echo ""

# 11. 验证删除
echo "11. 验证删除（GET /user/1）- 应该返回404"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/1" -w "\n状态码: %{http_code}\n"
echo ""

# 12. 测试错误情况 - 获取不存在的用户
echo "12. 测试错误情况 - 获取不存在的用户（GET /user/999）"
echo "-----------------------------------------"
curl -X GET "${BASE_URL}/999" -w "\n状态码: %{http_code}\n"
echo ""

echo "========================================="
echo "测试完成"
echo "========================================="
