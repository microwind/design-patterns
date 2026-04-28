from flask import Flask, g
from flask_cors import CORS

from app.config.config import Config
from app.models import db
from app.models.user import User
from app.models.order import Order
from app.repository.user_repository import UserRepository
from app.repository.order_repository import OrderRepository
from app.services.user_service import UserService
from app.services.order_service import OrderService
from app.controllers.user_controller import init_user_controller
from app.controllers.order_controller import init_order_controller
from app.middleware import (
    request_id_middleware,
    logging_middleware,
    cors_middleware,
    register_error_handlers
)


def create_app(config_path: str = None):
    """应用工厂模式"""
    app = Flask(__name__)
    
    # 加载配置
    config = Config(config_path)
    app.config['CONFIG'] = config
    
    # 配置数据库（暂时都使用 MySQL）
    app.config['SQLALCHEMY_BINDS'] = {
        'user': config.get_database_uri('user'),
        'order': config.get_database_uri('order')  # 暂时使用 MySQL
    }
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    
    # 初始化数据库
    db.init_app(app)
    
    # 注册中间件
    @app.before_request
    def before_request():
        request_id_middleware()
        logging_middleware()
    
    cors_middleware(app)
    register_error_handlers(app)
    
    # 初始化仓储
    user_repository = UserRepository()
    order_repository = OrderRepository()
    
    # 初始化服务
    user_service = UserService(user_repository)
    order_service = OrderService(order_repository)
    
    # 注册控制器
    init_user_controller(user_service)
    init_order_controller(order_service)
    
    # 注册蓝图
    from app.controllers.user_controller import user_bp
    from app.controllers.order_controller import order_bp
    app.register_blueprint(user_bp)
    app.register_blueprint(order_bp)
    
    # 健康检查端点
    @app.route('/health')
    def health_check():
        return {'status': 'healthy'}
    
    # 首页端点
    @app.route('/')
    def home():
        return {
            'message': 'Flask-MVC Scaffold',
            'version': '1.0.0'
        }
    
    return app
