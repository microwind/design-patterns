from flask import jsonify
from typing import Any, Optional


def success_response(data: Any = None, message: str = "success", code: int = 200):
    """创建标准化的成功响应"""
    return jsonify({
        'code': code,
        'message': message,
        'data': data
    }), code


def error_response(message: str, code: int = 400, data: Any = None):
    """创建标准化的错误响应"""
    return jsonify({
        'code': code,
        'message': message,
        'data': data
    }), code


def not_found_response(message: str = "Resource not found"):
    """创建标准化的未找到响应"""
    return error_response(message, 404)


def bad_request_response(message: str = "Bad request"):
    """创建标准化的错误请求响应"""
    return error_response(message, 400)


def internal_error_response(message: str = "Internal server error"):
    """创建标准化的内部错误响应"""
    return error_response(message, 500)
