# -*- coding: utf-8 -*-
import http.server
import socketserver
import requests
from src.interfaces.controllers.order_controller import OrderController
from src.application.services.order_service import OrderService
from src.infrastructure.repository.order_repository import OrderRepository
from src.middleware.logging_middleware import logging_middleware
from src.interfaces.routes.order_routes import order_routes

# 初始化依赖
order_repository = OrderRepository()
order_service = OrderService(order_repository)
order_controller = OrderController(order_service)

# 初始化路由
router = order_routes(order_controller, logging_middleware)

# 定义基础 URL
BASE_URL = 'http://localhost:8080'
API_PREFIX = '/api'

class MyHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        router['handle_request'](self, self)

    def do_POST(self):
        router['handle_request'](self, self)

    def do_PUT(self):
        router['handle_request'](self, self)

    def do_DELETE(self):
        router['handle_request'](self, self)


# 启动服务器
server = socketserver.TCPServer(("", 8080), MyHandler)
print('测试服务器已启动，端口：8080')

try:
    # 测试用例
    # 测试创建订单
    create_order_url = f'{BASE_URL}{API_PREFIX}/orders'
    create_order_data = {'customerName': '齐天大圣', 'amount': 99.99}
    create_order_response = requests.post(
        create_order_url, json=create_order_data)

    print('创建订单测试结果：')
    print('状态码:', create_order_response.status_code)
    print('响应体:', create_order_response.text)

    if create_order_response.status_code != 201:
        raise Exception('创建订单测试失败')

    # 测试获取订单
    order_id = create_order_response.json().get('id')
    get_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    get_order_response = requests.get(get_order_url)

    print('获取订单测试结果：')
    print('状态码:', get_order_response.status_code)
    print('响应体:', get_order_response.text)

    if get_order_response.status_code != 200:
        raise Exception('获取订单测试失败')

    # 测试更新订单
    update_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    update_order_data = {'customerName': '孙悟空', 'amount': 11.22}
    update_order_response = requests.put(
        update_order_url, json=update_order_data)

    print('更新订单测试结果：')
    print('状态码:', update_order_response.status_code)
    print('响应体:', update_order_response.text)

    if update_order_response.status_code != 200:
        raise Exception('更新订单测试失败')

    # 测试删除订单
    delete_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    delete_order_response = requests.delete(delete_order_url)

    print('删除订单测试结果：')
    print('状态码:', delete_order_response.status_code)

    if delete_order_response.status_code != 204:
        raise Exception('删除订单测试失败')

    print('所有测试通过！')
except Exception as error:
    print('测试失败：', error)
finally:
    # 关闭服务器
    server.shutdown()
    server.server_close()
    print('测试服务器已关闭')