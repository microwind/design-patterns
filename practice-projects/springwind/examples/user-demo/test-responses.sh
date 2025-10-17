#!/bin/bash

echo "启动服务器..."
java -jar target/springwind-user-demo-1.0-SNAPSHOT.jar --web &
PID=$!
echo "服务器 PID: $PID"

sleep 6

echo ""
echo "========== 测试各种响应类型 =========="
echo""

echo "1. 首页 (HtmlResult):"
curl -s http://localhost:8080/ | grep -o "<title>.*</title>"
echo ""

echo "2. JSON 响应:"
curl -s http://localhost:8080/demo/json
echo ""

echo "3. 文本响应:"
curl -s http://localhost:8080/demo/text | head -2
echo ""

echo "4. HTML 响应:"
curl -s http://localhost:8080/demo/html | grep -o "<h2>.*</h2>"
echo ""

echo "5. 自定义 Content-Type (XML):"
curl -i -s http://localhost:8080/demo/custom | grep "Content-Type"
echo ""

echo "6. 重定向 (应该是 302):"
curl -i -s http://localhost:8080/demo/redirect | grep "HTTP\|Location"
echo ""

echo "========== 测试完成 =========="
echo "停止服务器..."
kill $PID 2>/dev/null
