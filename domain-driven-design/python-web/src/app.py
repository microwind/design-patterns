# -*- coding: utf-8 -*-
import logging
import sys
import os
import http.server
import socketserver
import socket  # 用于检测端口占用
from urllib.parse import urlparse

# 设定目录为包路径
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

from src.interfaces.routes.order_routes import order_routes
from src.interfaces.controllers.order_controller import OrderController
from src.application.services.order_service import OrderService
from src.infrastructure.repository.order_repository_impl import OrderRepositoryImpl as OrderRepository 
from src.utils.logging import setup_logging
from src.config.server_config import PORT, LOGGING

# 默认日志文件
log_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../' + LOGGING['file'])
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler(), logging.FileHandler(log_file, encoding="utf-8")],
    force=True  # 强制重新配置
)

#""" 以下修复端口占用问题
def is_port_in_use(port):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        return s.connect_ex(('localhost', port)) == 0  # 端口被占用返回 True

if is_port_in_use(PORT):
    logging.error(f"Port {PORT} is already in use. Please stop the existing process or use another port.")
    sys.exit(1)  # 终止程序
#"""
    
# 初始化依赖
order_repository = OrderRepository()
order_service = OrderService(order_repository)
order_controller = OrderController(order_service)

# 初始化路由
router = order_routes(order_controller)

# 初始化自定义日志文件
setup_logging(os.path.join(os.path.dirname(os.path.abspath(__file__)), '../middleware.log'))

# MainHandler类，启动HTTP server
class MainHandler(http.server.BaseHTTPRequestHandler):

    def log_message(self, format, *args):
        logging.info("%s - - [%s] %s\n" %
                     (self.client_address[0],
                      self.log_date_time_string(),
                      format % args))


    def handle_request(self):
        if self.path == '/favicon.ico':
            self.send_response(204)
            return

        parsed_url = urlparse(self.path)
        method = self.command
        path = parsed_url.path
        #logging.debug(f"Received request: {method} {path}")

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
                创建：curl -X POST "http://localhost:{PORT}/api/orders" -H "Content-Type: application/json" -d '{{"customer_name": "齐天大圣", "amount": 99.99}}'
                查询：curl -X GET "http://localhost:{PORT}/api/orders/订单号"
                更新：curl -X PUT "http://localhost:{PORT}/api/orders/订单号" -H "Content-Type: application/json" -d '{{"customer_name": "孙悟空", "amount": 11.22}}'
                删除：curl -X DELETE "http://localhost:{PORT}/api/orders/订单号"
                查询全部：curl -X GET "http://localhost:{PORT}/api/orders"
                </code>
                详细：https://github.com/microwind/design-patterns/tree/main/domain-driven-design
            </pre>
            """
            self.wfile.write(response.encode('utf-8'))
            return

        # 处理其他 API 路由
        handler = router.match_route(method, path)
        # logging.debug(f"Route match result: Handler: {handler}")

        if not handler:
            logging.warning(f"No matching route found for {method} {path}")
            self.send_error(404, "Not Found")
            return

        try:
            # 调用包装后的处理函数，将 self 同时作为 req 和 resp 传递
            handler(self, self)
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

"""
jarry@MacBook-Pro python-web % python src/app.py
2022-02-22 14:40:07,037 - INFO - Starting server on :8080 successfully.
# 执行curl例子，可以看到控制台输出
127.0.0.1 - - [22/Feb/2022 14:40:09] "GET / HTTP/1.1" 200 -
127.0.0.1 - - [22/Feb/2022 14:40:09] "GET /favicon.ico HTTP/1.1" 204 -
127.0.0.1 - - [22/Feb/2022 14:40:17] "POST /api/orders HTTP/1.1" 201 -
127.0.0.1 - - [22/Feb/2022 14:40:24] "GET /api/orders/1740379217367713 HTTP/1.1" 200 -
2022-02-22 14:40:51,308 - INFO - 订单 ID 1740379217367713 的客户名称已更新为: 孙悟空
2022-02-22 14:40:51,309 - INFO - 订单 ID 1740379217367713 的金额已更新为: 11.22
127.0.0.1 - - [22/Feb/2022 14:40:51] "PUT /api/orders/1740379217367713 HTTP/1.1" 200 -
127.0.0.1 - - [22/Feb/2022 14:41:07] "GET /api/orders/1740379217367713 HTTP/1.1" 200 -
127.0.0.1 - - [22/Feb/2022 14:41:15] "DELETE /api/orders/1740379217367713 HTTP/1.1" 204 -
127.0.0.1 - - [22/Feb/2022 14:41:15] "DELETE /api/orders/1740379217367713 HTTP/1.1" 404 -
127.0.0.1 - - [22/Feb/2022 14:41:17] "GET /api/orders/1740379217367713 HTTP/1.1" 404 -

"""