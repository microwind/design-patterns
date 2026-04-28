from flask import Blueprint, request, jsonify
from app.services.user_service import UserService

user_bp = Blueprint('users', __name__, url_prefix='/api/users')


def init_user_controller(user_service: UserService):
    """初始化用户控制器路由"""
    
    @user_bp.route('', methods=['POST'])
    def create_user():
        """创建新用户"""
        data = request.get_json()
        try:
            result = user_service.create_user(
                name=data.get('name'),
                email=data.get('email'),
                phone=data.get('phone')
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

    @user_bp.route('', methods=['GET'])
    def get_all_users():
        """获取所有用户"""
        try:
            page = request.args.get('page', 1, type=int)
            per_page = request.args.get('per_page', 10, type=int)
            
            # 如果有分页参数，使用分页查询
            if page or per_page:
                result = user_service.get_users_paginated(page, per_page)
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': result
                }), 200
            else:
                users = user_service.get_all_users()
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': users
                }), 200
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @user_bp.route('/<int:user_id>', methods=['GET'])
    def get_user(user_id):
        """根据 ID 获取用户"""
        try:
            user = user_service.get_user(user_id)
            if user:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': user
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'User not found',
                    'data': None
                }), 404
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @user_bp.route('/<int:user_id>/email', methods=['PUT'])
    def update_user_email(user_id):
        """更新用户邮箱"""
        data = request.get_json()
        try:
            user = user_service.update_user_email(user_id, data.get('email'))
            if user:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': user
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'User not found',
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

    @user_bp.route('/<int:user_id>/phone', methods=['PUT'])
    def update_user_phone(user_id):
        """更新用户手机号"""
        data = request.get_json()
        try:
            user = user_service.update_user_phone(user_id, data.get('phone'))
            if user:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': user
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'User not found',
                    'data': None
                }), 404
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @user_bp.route('/<int:user_id>', methods=['DELETE'])
    def delete_user(user_id):
        """根据 ID 删除用户"""
        try:
            success = user_service.delete_user(user_id)
            if success:
                return jsonify({
                    'code': 200,
                    'message': 'success',
                    'data': None
                }), 200
            else:
                return jsonify({
                    'code': 404,
                    'message': 'User not found',
                    'data': None
                }), 404
        except Exception as e:
            return jsonify({
                'code': 500,
                'message': f'Internal server error: {str(e)}',
                'data': None
            }), 500

    @user_bp.route('/<int:user_id>/orders', methods=['GET'])
    def get_user_orders(user_id):
        """根据用户 ID 获取订单（占位符，后续将连接订单服务）"""
        return jsonify({
            'code': 200,
            'message': 'success',
            'data': []
        }), 200

    return user_bp
