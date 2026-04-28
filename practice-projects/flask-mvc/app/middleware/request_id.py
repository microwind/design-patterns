import uuid
from flask import request, g


def request_id_middleware():
    """Add unique request ID to each request for tracing"""
    request_id = request.headers.get('X-Request-ID', str(uuid.uuid4()))
    g.request_id = request_id
