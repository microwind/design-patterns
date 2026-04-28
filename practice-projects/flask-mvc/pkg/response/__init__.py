from flask import jsonify
from typing import Any, Optional


def success_response(data: Any = None, message: str = "success", code: int = 200):
    """Create a standardized success response"""
    return jsonify({
        'code': code,
        'message': message,
        'data': data
    }), code


def error_response(message: str, code: int = 400, data: Any = None):
    """Create a standardized error response"""
    return jsonify({
        'code': code,
        'message': message,
        'data': data
    }), code


def not_found_response(message: str = "Resource not found"):
    """Create a standardized not found response"""
    return error_response(message, 404)


def bad_request_response(message: str = "Bad request"):
    """Create a standardized bad request response"""
    return error_response(message, 400)


def internal_error_response(message: str = "Internal server error"):
    """Create a standardized internal error response"""
    return error_response(message, 500)
