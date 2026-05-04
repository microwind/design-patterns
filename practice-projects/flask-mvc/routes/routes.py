# routes/routes.py
from controllers.user_controller import user_bp
from controllers.order_controller import order_bp

def init_app(app):
    """注册所有蓝图"""
    app.register_blueprint(user_bp)
    app.register_blueprint(order_bp)