# -*- coding: utf-8 -*-
import logging
import sys
import os
import threading
import time
import requests
import signal
import os

# 将项目根目录添加到 sys.path
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '../../../'))
sys.path.insert(0, project_root)

# 打印 sys.path 确保路径添加无误
print("Updated sys.path:", sys.path)

# 引入 app 的 run_server 方法
from src.app import run_server

BASE_URL = 'http://localhost:8080'
API_PREFIX = '/api'

# 日志配置
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler()]  # 确保日志输出到控制台
)

# 启动服务器（使用线程）
def start_server():
    server_thread = threading.Thread(target=run_server, daemon=True)
    server_thread.start()
    time.sleep(0.1)  # 给服务器时间启动
    return server_thread

# 关闭服务器
def stop_server():
    # 使用 kill 信号来终止服务器进程
    os.kill(os.getpid(), signal.SIGTERM)

# 启动服务器
server_thread = start_server()

# 创建订单请求
def test_create_order():
    print("开始测试创建订单...")
    create_order_url = f'{BASE_URL}{API_PREFIX}/orders'
    create_order_data = {'customer_name': '齐天大圣', 'amount': 99.99}
    create_order_response = requests.post(create_order_url, json=create_order_data)

    print('创建订单状态码:', create_order_response.status_code)
    print('创建订单响应体:', create_order_response.text)

    if create_order_response.status_code != 201:
        raise Exception('创建订单测试失败')
    return create_order_response.json()['id']  # 返回创建的订单ID

# 获取订单请求
def test_get_order(order_id):
    get_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    get_order_response = requests.get(get_order_url)

    print('获取订单状态码:', get_order_response.status_code)
    print('获取订单响应体:', get_order_response.text)

    if get_order_response.status_code != 200:
        raise Exception('获取订单测试失败')

# 获取订单列表请求
def test_get_orders(user_id):
    print(f'test_get_orders[user_id:{user_id}]')
    get_orders_url = f'{BASE_URL}{API_PREFIX}/orders'
    get_orders_response = requests.get(get_orders_url)

    print('获取订单列表状态码:', get_orders_response.status_code)
    print('获取订单列表响应体:', get_orders_response.text)

    if get_orders_response.status_code != 200:
        raise Exception('获取订单列表测试失败')

# 更新订单请求
def test_update_order(order_id):
    update_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    update_order_data = {'customer_name': '孙悟空', 'amount': 11.22}
    update_order_response = requests.put(update_order_url, json=update_order_data)

    print('更新订单状态码:', update_order_response.status_code)
    print('更新订单响应体:', update_order_response.text)

    if update_order_response.status_code != 200:
        raise Exception('更新订单测试失败')

# 删除订单请求
def test_delete_order(order_id):
    delete_order_url = f'{BASE_URL}{API_PREFIX}/orders/{order_id}'
    delete_order_response = requests.delete(delete_order_url)

    print('删除订单状态码:', delete_order_response.status_code)

    if delete_order_response.status_code != 204:
        raise Exception('删除订单测试失败')

# 执行测试用例
try:
    order_id = test_create_order()  # 创建订单并获取 ID
    test_get_order(order_id)
    test_update_order(order_id)
    test_get_orders(10001)
    test_delete_order(order_id)

    logging.info("所有测试通过！")
except Exception as e:
    logging.error(f"测试失败: {e}")

finally:
    # 测试完成后关闭服务器
    stop_server()

"""
# 执行测试
python-web % python tests/interfaces/routes/order_routes.test.py
"""