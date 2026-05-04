# utils/middleware.py
import uuid
import logging
import time
from flask import request, g
from flask_cors import CORS

logger = logging.getLogger(__name__)

def request_id_middleware():
    """请求ID中间件"""
    g.request_id = str(uuid.uuid4())
    g.start_time = time.time()

def logging_middleware():
    """日志中间件"""
    logger.info(f"[{request.method}] {request.path} - Request ID: {g.request_id}")

def cors_middleware(app):
    """CORS中间件"""
    CORS(app, resources={
        r"/api/*": {
            "origins": "*",
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Content-Type", "Authorization", "X-Request-ID"]
        }
    })

def register_error_handlers(app):
    """注册错误处理器"""
    
    @app.errorhandler(400)
    def bad_request(error):
        from utils.response import error_response
        return error_response("请求参数错误", code=400)
    
    @app.errorhandler(404)
    def not_found(error):
        from utils.response import error_response
        return error_response("资源不存在", code=404)
    
    @app.errorhandler(500)
    def internal_error(error):
        from utils.response import error_response
        logger.error(f"Internal server error: {error}")
        return error_response("服务器内部错误", code=500)
    
    @app.errorhandler(Exception)
    def handle_exception(error):
        from utils.response import error_response
        logger.exception(f"Unhandled exception: {error}")
        return error_response("系统异常", code=500)

def init_middleware(app):
    """初始化所有中间件"""
    @app.before_request
    def before_request():
        request_id_middleware()
        logging_middleware()

    @app.after_request
    def after_request(response):
        duration = round((time.time() - g.start_time) * 1000, 2)
        logger.info(f"[{request.method}] {request.path} - Completed in {duration}ms")
        
        # 添加请求ID到响应头
        response.headers['X-Request-ID'] = getattr(g, 'request_id', '')
        return response
    
    cors_middleware(app)
    register_error_handlers(app)