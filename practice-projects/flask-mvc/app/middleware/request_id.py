import uuid
from flask import request, g


def request_id_middleware():
    """为每个请求添加唯一的请求 ID，用于追踪"""
    request_id = request.headers.get('X-Request-ID', str(uuid.uuid4()))
    g.request_id = request_id
