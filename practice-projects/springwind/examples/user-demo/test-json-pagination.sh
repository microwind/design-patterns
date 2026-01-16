#!/bin/bash

# 测试 User JSON 返回和分页查询
# 使用 jq 工具美化 JSON 输出（如果没有 jq，可以去掉 | jq）

BASE_URL="http://localhost:8080/user"

echo "========================================="
echo "测试 User JSON 返回和分页查询"
echo "========================================="
echo ""

# 1. 创建测试用户
echo "1. 创建测试用户"
echo "-----------------------------------------"
for i in {1..15}; do
    curl -s -X POST "${BASE_URL}" \
      -H "Content-Type: application/json" \
      -d "{\"name\":\"user${i}\",\"email\":\"user${i}@example.com\",\"phone\":\"1380013800${i}\"}" > /dev/null
    echo "Created user${i}"
done
echo ""

# 2. 获取单个用户（验证 JSON 对象格式）
echo "2. 获取单个用户（ID=1） - 验证 JSON 对象格式"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}/1")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 3. 获取所有用户（验证 JSON 数组）
echo "3. 获取所有用户列表 - 验证 JSON 数组"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}")
echo "$response" | python3 -m json.tool 2>/dev/null | head -40 || echo "$response" | head -40
echo "..."
echo ""

# 4. 分页查询 - 第1页（每页5条）
echo "4. 分页查询 - 第1页（每页5条）"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}?page=1&pageSize=5")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 5. 分页查询 - 第2页（每页5条）
echo "5. 分页查询 - 第2页（每页5条）"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}?page=2&pageSize=5")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 6. 分页查询 - 第3页（每页5条）
echo "6. 分页查询 - 第3页（每页5条）"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}?page=3&pageSize=5")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 7. 验证分页数据结构
echo "7. 验证分页数据结构"
echo "-----------------------------------------"
echo "检查分页响应中的字段："
response=$(curl -s "${BASE_URL}?page=1&pageSize=3")
echo "$response" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print('- code:', data.get('code'))
print('- message:', data.get('message'))
if 'data' in data:
    page_data = data['data']
    print('- data.list: 存在, 包含', len(page_data.get('list', [])), '条记录')
    print('- data.page:', page_data.get('page'))
    print('- data.pageSize:', page_data.get('pageSize'))
    print('- data.total:', page_data.get('total'))
    print('- data.totalPages:', page_data.get('totalPages'))
    print('- data.hasNext:', page_data.get('hasNext'))
    print('- data.hasPrevious:', page_data.get('hasPrevious'))
" 2>/dev/null || echo "无法解析 JSON（需要安装 Python 3）"
echo ""

# 8. 根据用户名查询
echo "8. 根据用户名查询"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}/name/user5")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

# 9. 获取用户总数
echo "9. 获取用户总数"
echo "-----------------------------------------"
response=$(curl -s "${BASE_URL}/count")
echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
echo ""

echo "========================================="
echo "测试完成"
echo ""
echo "预期结果："
echo "1. 单个用户返回包含完整字段的 JSON 对象"
echo "2. 用户列表返回 JSON 数组"
echo "3. 分页查询返回包含 list, page, pageSize, total 等字段"
echo "4. 所有响应都是标准 JSON 格式"
echo "========================================="
