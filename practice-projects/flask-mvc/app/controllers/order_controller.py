from flask import Blueprint, request, jsonify
from app.services.order_service import OrderService

order_bp = Blueprint('orders', __name__, url_prefix='/api/orders')


def init_order_controller(order_service: OrderService):
    """初始化订单控制器路由"""
    
    @order_bp.route('', methods=['POST'])
    def create_order():
        """创建新订单"""
        data = request.get_json()
        try:
            result = order_service.create_order(
                user_id=data.get('user_id'),
                total_amount=data.get('total_amount')
            )
            return jsonify({
                'code': 200,
                'message': 'success',
                'data': result
            }), 201
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('', methods=['GET'])
    def get_all_orders():
        """获取所有订单"""
        try:
            page = request.args.get('page', 1, type=int)
            per_page = request.args.get('per_page', 10, type=int)
            
            # 如果有分页参数，使用分页查询
            if page or per_page:
                result = order_service.get_orders_paginated(page, per_page)
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': result
                }), 200
            else:
                orders = order_service.get_all_orders()
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': orders
                }), 200
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>', methods=['GET'])
    def get_order(order_id):
        """根据 ID 获取订单"""
        try:
            order = order_service.get_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>/pay', methods=['PUT'])
    def pay_order(order_id):
        """支付订单"""
        try:
            order = order_service.pay_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>/ship', methods=['PUT'])
    def ship_order(order_id):
        """发货"""
        try:
            order = order_service.ship_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>/deliver', methods=['PUT'])
    def deliver_order(order_id):
        """送达"""
        try:
            order = order_service.deliver_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>/cancel', methods=['PUT'])
    def cancel_order(order_id):
        """取消订单"""
        try:
            order = order_service.cancel_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @order_bp.route('/<int:order_id>/refund', methods=['PUT'])
    def refund_order(order_id):
        """退款"""
        try:
            order = order_service.refund_order(order_id)
            if order:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': order
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'Order not found',
                    'data': None
                }), 404
        except ValueError as e:
            return jsonify({
                'code': 400,
                'message': str(e),
                'data': None
            }), 400
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    return order_bp
