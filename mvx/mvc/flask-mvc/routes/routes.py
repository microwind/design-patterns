# router/routes.py
from flask import Blueprint
from controllers import HomeController

bp = Blueprint('', __name__, url_prefix='/api/orders')

@bp.route('/<int:order_id>', methods=['GET'])
def get_order(order_id):
    return HomeController.get_order(order_id)

@bp.route('', methods=['POST'])
def create_order():
    return HomeController.create_order()

# 注册蓝图
def init_app(app):
    app.register_blueprint(bp)