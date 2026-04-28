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
    """Application factory pattern"""
    app = Flask(__name__)
    
    # Load configuration
    config = Config(config_path)
    app.config['CONFIG'] = config
    
    # Configure databases
    app.config['SQLALCHEMY_BINDS'] = {
        'user': config.get_database_uri('user'),
        'order': config.get_database_uri('order')
    }
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    
    # Initialize database
    db.init_app(app)
    
    # Register middleware
    @app.before_request
    def before_request():
        request_id_middleware()
        logging_middleware()
    
    cors_middleware(app)
    register_error_handlers(app)
    
    # Initialize repositories
    user_repository = UserRepository()
    order_repository = OrderRepository()
    
    # Initialize services
    user_service = UserService(user_repository)
    order_service = OrderService(order_repository)
    
    # Register controllers
    init_user_controller(user_service)
    init_order_controller(order_service)
    
    # Register blueprints
    from app.controllers.user_controller import user_bp
    from app.controllers.order_controller import order_bp
    app.register_blueprint(user_bp)
    app.register_blueprint(order_bp)
    
    # Health check endpoint
    @app.route('/health')
    def health_check():
        return {'status': 'healthy'}
    
    # Home endpoint
    @app.route('/')
    def home():
        return {
            'message': 'Flask-MVC Scaffold',
            'version': '1.0.0'
        }
    
    return app
