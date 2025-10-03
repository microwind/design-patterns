# 舞界AI舞蹈算法检测

# 安装依赖
$ pip install -r requirements.txt

# 设置环境变量
$ export FLASK_ENV=development
$ export FLASK_APP=app.py

# 启动开发服务器
$ flask run

# 访问接口
$ curl http://localhost:5000/api/orders/1
$ curl -X POST http://localhost:5000/api/orders -H "Content-Type: application/json" -d '{"order_no": "20240501"}'

# 运行测试
$ pytest