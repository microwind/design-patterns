from flask import Blueprint, request, jsonify
from app.services.user_service import UserService

user_bp = Blueprint('users', __name__, url_prefix='/api/users')


def init_user_controller(user_service: UserService):
    """Initialize user controller routes"""
    
    @user_bp.route('', methods=['POST'])
    def create_user():
        """Create a new user"""
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
        """Get all users"""
        try:
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
        """Get user by ID"""
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
        """Update user email"""
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
        """Update user phone"""
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
        """Delete user by ID"""
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
        """Get orders by user ID (placeholder, will be connected to order service)"""
        return jsonify({
            'code': 200,
            'message': 'success',
            'data': []
        }), 200

    return user_bp
