# -*- coding: utf-8 -*-
import logging
import sys
import os
import http.server
import socketserver
import socket  # 新增，用于检测端口占用
from urllib.parse import urlparse

# 设定目录为包路径
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from src.interfaces.routes.order_routes import order_routes
from src.interfaces.controllers.order_controller import OrderController
from src.application.services.order_service import OrderService
from src.infrastructure.repository.order_repository import OrderRepository
from src.middleware.logging_middleware import logging_middleware
from src.config.server_config import PORT

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler()]  # 确保日志输出到控制台
)

# 2. **修复端口占用问题**
def is_port_in_use(port):
    """检测端口是否被占用"""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        return s.connect_ex(('localhost', port)) == 0  # 端口被占用返回 True

if is_port_in_use(PORT):
    logging.error(f"Port {PORT} is already in use. Please stop the existing process or use another port.")
    sys.exit(1)  # 终止程序

# 初始化依赖
order_repository = OrderRepository()
order_service = OrderService(order_repository)
order_controller = OrderController(order_service)

# 初始化路由
router = order_routes(order_controller, logging_middleware)


class MainHandler(http.server.SimpleHTTPRequestHandler):
    def handle_request(self):
        parsed_url = urlparse(self.path)
        method = self.command
        path = parsed_url.path
        logging.debug(f"Received request: {method} {path}")

        # 处理首页路由
        if method == 'GET' and path == '/':
            self.send_response(200)
            self.send_header('Content-Type', 'text/html; charset=utf-8')
            self.end_headers()
            response = f"""
            <h1>Welcome to DDD example.</h1>
            <pre>
                测试
                <code>
                创建：curl -X POST "http://localhost:{PORT}/api/orders" -H "Content-Type: application/json" -d '{{"customerName": "齐天大圣", "amount": 99.99}}'
                查询：curl -X GET "http://localhost:{PORT}/api/orders/订单号"
                更新：curl -X PUT "http://localhost:{PORT}/api/orders/订单号" -H "Content-Type: application/json" -d '{{"customerName": "孙悟空", "amount": 11.22}}'
                删除：curl -X DELETE "http://localhost:{PORT}/api/orders/订单号"
                查询：curl -X GET "http://localhost:{PORT}/api/orders/订单号"
                </code>
            </pre>
            """
            self.wfile.write(response.encode('utf-8'))
            return

        # 处理其他 API 路由
        route = router.match_route(method, path)
        logging.debug(f"Route match result: {route}")

        if not route:
            logging.warning(f"No matching route found for {method} {path}")
            self.send_error(404, "Not Found")
            return

        middleware, handler = route
        logging.debug(f"Middleware: {middleware}, Handler: {handler}")

        if not handler:
            logging.error(f"Handler is None for {method} {path}")
            self.send_error(500, "Handler is None")
            return

        try:
            if middleware:
                middleware(self)  # 执行中间件
            handler(self)  # 执行处理函数
        except Exception as e:
            logging.error(f"Error handling request: {e}")
            self.send_error(500, "Internal Server Error")

    def do_GET(self):
        self.handle_request()

    def do_POST(self):
        self.handle_request()

    def do_PUT(self):
        self.handle_request()

    def do_DELETE(self):
        self.handle_request()

def run_server():
    with socketserver.TCPServer(("", PORT), MainHandler) as httpd:
        logging.info(f"Starting server on :{PORT} successfully.")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            logging.info("Server shutting down...")
            httpd.server_close()


if __name__ == "__main__":
    run_server()
