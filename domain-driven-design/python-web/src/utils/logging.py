# -*- coding: utf-8 -*-
import os
import time
from src.config.server_config import LOGGING

# 全局变量，用于存储日志文件路径
log_file_path = None

# 设置日志文件路径
def setup_logging(log_file):
    global log_file_path
    log_file_path = log_file

    # 确保日志文件存在
    if not os.path.exists(log_file_path):
        with open(log_file_path, 'w'): pass  # 创建空文件
    print(f"日志系统初始化完成，日志写入: {log_file_path}")

# 记录请求日志
def log_request(req, start_time):
    duration = (time.time() - start_time) * 1000  # 转换为毫秒
    log_message = f"REQUEST: {req.command} {req.path} took {duration:.2f}ms\n"
    log_to_file(log_message)

# 记录普通信息日志
def log_info(message):
    log_message = f"INFO: {message}\n"
    log_to_file(log_message)

# 记录错误日志
def log_error(message):
    log_message = f"ERROR: {message}\n"
    log_to_file(log_message)

# 将日志消息写入文件
def log_to_file(message):
    print('log_to_file:', message)
    # global log_file_path
    # log_file_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), LOGGING['file'])
    # print('log_to_file:', log_file_path)
    # if log_file_path:
    #     with open(log_file_path, 'a', encoding='utf-8') as log_file:
    #         log_file.write(message)
    # else:
    #     print("日志文件路径未初始化，无法写入日志。")
