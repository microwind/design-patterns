from flask import Flask
from utils.config import Config
from utils.extensions import db
from utils.middleware import init_middleware
from routes.routes import init_app as init_routes
from utils.events import get_event_bus

def create_app(config_path: str = None):
    """应用工厂模式"""
    app = Flask(__name__)
    
    # 加载配置
    config = Config(config_path)
    app.config['CONFIG'] = config
    
    # 配置数据库（双数据库）
    app.config['SQLALCHEMY_DATABASE_URI'] = config.get_database_uri('user')
    app.config['SQLALCHEMY_BINDS'] = {
        'user': config.get_database_uri('user'),
        'order': config.get_database_uri('order')
    }
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    
    # 初始化扩展
    db.init_app(app)
    
    # 初始化中间件
    init_middleware(app)
    
    # 注册路由
    init_routes(app)
    
    # 健康检查端点
    @app.route('/health')
    def health_check():
        return {'status': 'healthy', 'version': '1.0.0'}
    
    # 首页端点
    @app.route('/')
    def home():
        return {
            'message': 'Flask-MVC Scaffold with DDD Features',
            'version': '1.0.0',
            'features': ['MVC Architecture', 'Dual Database', 'Event-Driven']
        }
    
    # 创建数据库表
    with app.app_context():
        db.create_all()
    
    return app

if __name__ == '__main__':
    app = create_app()
    app.run(debug=True, host='0.0.0.0', port=5000)
