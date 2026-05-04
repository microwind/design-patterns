from flask import Blueprint, request, jsonify
from services.order_service import OrderService
from schemas.order_schema import OrderSchema, CreateOrderSchema
from utils.response import success_response, error_response
import logging

logger = logging.getLogger(__name__)

# 创建订单蓝图
order_bp = Blueprint('order', __name__, url_prefix='/api/orders')

# 全局订单服务实例
order_service = OrderService()

@order_bp.route('/', methods=['GET'])
def get_orders():
    """获取订单列表"""
    try:
        orders = order_service.get_all_orders()
        schema = OrderSchema(many=True)
        return success_response(data=schema.dump(orders))
    except Exception as e:
        logger.error(f"获取订单列表失败: {e}")
        return error_response(message="获取订单列表失败")

@order_bp.route('/<int:order_id>', methods=['GET'])
def get_order(order_id):
    """获取订单详情"""
    try:
        order = order_service.get_order_by_id(order_id)
        if not order:
            return error_response(message="订单不存在", code=404)
        
        schema = OrderSchema()
        return success_response(data=schema.dump(order))
    except Exception as e:
        logger.error(f"获取订单详情失败: {e}")
        return error_response(message="获取订单详情失败")

@order_bp.route('/user/<int:user_id>', methods=['GET'])
def get_user_orders(user_id):
    """获取用户订单列表"""
    try:
        orders = order_service.get_user_orders(user_id)
        schema = OrderSchema(many=True)
        return success_response(data=schema.dump(orders))
    except Exception as e:
        logger.error(f"获取用户订单失败: {e}")
        return error_response(message="获取用户订单失败")

@order_bp.route('/', methods=['POST'])
def create_order():
    """创建订单"""
    try:
        # 数据验证
        schema = CreateOrderSchema()
        data = schema.load(request.get_json())
        
        # 创建订单
        order = order_service.create_order(data)
        order_schema = OrderSchema()
        
        return success_response(
            data=order_schema.dump(order),
            message="订单创建成功",
            code=0
        )
    except Exception as e:
        logger.error(f"创建订单失败: {e}")
        return error_response(message=str(e))

@order_bp.route('/<int:order_id>/pay', methods=['PUT'])
def pay_order(order_id):
    """支付订单"""
    try:
        order = order_service.pay_order(order_id)
        schema = OrderSchema()
        return success_response(
            data=schema.dump(order),
            message="订单支付成功"
        )
    except Exception as e:
        logger.error(f"支付订单失败: {e}")
        return error_response(message=str(e))

@order_bp.route('/<int:order_id>/ship', methods=['PUT'])
def ship_order(order_id):
    """发货订单"""
    try:
        order = order_service.ship_order(order_id)
        schema = OrderSchema()
        return success_response(
            data=schema.dump(order),
            message="订单发货成功"
        )
    except Exception as e:
        logger.error(f"发货订单失败: {e}")
        return error_response(message=str(e))

@order_bp.route('/<int:order_id>/cancel', methods=['PUT'])
def cancel_order(order_id):
    """取消订单"""
    try:
        order = order_service.cancel_order(order_id)
        schema = OrderSchema()
        return success_response(
            data=schema.dump(order),
            message="订单取消成功"
        )
    except Exception as e:
        logger.error(f"取消订单失败: {e}")
        return error_response(message=str(e))

@order_bp.route('/<int:order_id>', methods=['DELETE'])
def delete_order(order_id):
    """删除订单"""
    try:
        success = order_service.delete_order(order_id)
        if success:
            return success_response(message="订单删除成功")
        else:
            return error_response(message="订单不存在", code=404)
    except Exception as e:
        logger.error(f"删除订单失败: {e}")
        return error_response(message="删除订单失败")

@order_bp.route('/count/user/<int:user_id>', methods=['GET'])
def get_user_order_count(user_id):
    """获取用户订单数量"""
    try:
        count = order_service.get_order_count_by_user(user_id)
        return success_response(data={'count': count})
    except Exception as e:
        logger.error(f"获取用户订单数量失败: {e}")
        return error_response(message="获取用户订单数量失败")