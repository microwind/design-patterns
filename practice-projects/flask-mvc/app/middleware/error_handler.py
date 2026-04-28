from flask import jsonify
from pkg.logger import get_logger

logger = get_logger(__name__)


def register_error_handlers(app):
    """注册全局错误处理器"""
    
    @app.errorhandler(400)
    def bad_request(error):
        logger.error(f"Bad request: {str(error)}")
        return jsonify({
            'code': 400,
            'message': '请求参数错误',
            'data': None
        }), 400

    @app.errorhandler(404)
    def not_found(error):
        logger.error(f"Not found: {str(error)}")
        return jsonify({
            'code': 404,
            'message': '资源未找到',
            'data': None
        }), 404

    @app.errorhandler(405)
    def method_not_allowed(error):
        logger.error(f"Method not allowed: {str(error)}")
        return jsonify({
            'code': 405,
            'message': '请求方法不允许',
            'data': None
        }), 405

    @app.errorhandler(500)
    def internal_error(error):
        logger.error(f"Internal error: {str(error)}")
        return jsonify({
            'code': 500,
            'message': '服务器内部错误',
            'data': None
        }), 500

    @app.errorhandler(Exception)
    def handle_exception(error):
        logger.error(f"Unhandled exception: {str(error)}")
        return jsonify({
            'code': 500,
            'message': '服务器内部错误',
            'data': None
        }), 500
