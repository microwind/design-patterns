import logging
from flask import g, request
from pkg.logger import get_logger

logger = get_logger(__name__)


def logging_middleware():
    """记录请求信息"""
    logger.info(
        f"Request: {request.method} {request.path} - "
        f"Request ID: {g.get('request_id', 'N/A')} - "
        f"Remote: {request.remote_addr}"
    )
