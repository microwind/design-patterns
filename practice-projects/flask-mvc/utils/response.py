# utils/response.py
from flask import jsonify

def api_response(data=None, message='success', code=0, status=200):
    """统一API响应格式"""
    return jsonify({
        'code': code,
        'message': message,
        'data': data,
        'success': code == 0
    }), status

def success_response(data=None, message='操作成功', code=0):
    """成功响应"""
    return api_response(data=data, message=message, code=code, status=200)

def error_response(message='操作失败', code=1, status=400):
    """错误响应"""
    return api_response(data=None, message=message, code=code, status=status)