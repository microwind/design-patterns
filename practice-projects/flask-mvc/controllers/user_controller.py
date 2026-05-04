from flask import Blueprint, request
from services.user_service import UserService
from schemas.user_schema import UserSchema, CreateUserSchema, UpdateUserSchema
from utils.response import success_response, error_response
import logging

logger = logging.getLogger(__name__)

# 创建用户蓝图
user_bp = Blueprint('user', __name__, url_prefix='/api/users')

# 全局用户服务实例
user_service = UserService()

@user_bp.route('/', methods=['GET'])
def get_users():
    """获取用户列表"""
    try:
        users = user_service.get_all_users()
        schema = UserSchema(many=True)
        return success_response(data=schema.dump(users))
    except Exception as e:
        logger.error(f"获取用户列表失败: {e}")
        return error_response(message="获取用户列表失败")

@user_bp.route('/<int:user_id>', methods=['GET'])
def get_user(user_id):
    """获取用户详情"""
    try:
        user = user_service.get_user_by_id(user_id)
        if not user:
            return error_response(message="用户不存在", code=404)
        
        schema = UserSchema()
        return success_response(data=schema.dump(user))
    except Exception as e:
        logger.error(f"获取用户详情失败: {e}")
        return error_response(message="获取用户详情失败")

@user_bp.route('/email/<email>', methods=['GET'])
def get_user_by_email(email):
    """根据邮箱获取用户"""
    try:
        user = user_service.get_user_by_email(email)
        if not user:
            return error_response(message="用户不存在", code=404)
        
        schema = UserSchema()
        return success_response(data=schema.dump(user))
    except Exception as e:
        logger.error(f"获取用户失败: {e}")
        return error_response(message="获取用户失败")

@user_bp.route('/', methods=['POST'])
def create_user():
    """创建用户"""
    try:
        # 数据验证
        schema = CreateUserSchema()
        data = schema.load(request.get_json())
        
        # 创建用户
        user = user_service.create_user(data)
        user_schema = UserSchema()
        
        return success_response(
            data=user_schema.dump(user),
            message="用户创建成功",
            code=0
        )
    except Exception as e:
        logger.error(f"创建用户失败: {e}")
        return error_response(message=str(e))

@user_bp.route('/<int:user_id>', methods=['PUT'])
def update_user(user_id):
    """更新用户"""
    try:
        # 数据验证
        schema = UpdateUserSchema()
        data = schema.load(request.get_json())
        
        # 更新用户
        user = user_service.update_user(user_id, data)
        user_schema = UserSchema()
        
        return success_response(
            data=user_schema.dump(user),
            message="用户更新成功"
        )
    except Exception as e:
        logger.error(f"更新用户失败: {e}")
        return error_response(message=str(e))

@user_bp.route('/<int:user_id>', methods=['DELETE'])
def delete_user(user_id):
    """删除用户"""
    try:
        success = user_service.delete_user(user_id)
        if success:
            return success_response(message="用户删除成功")
        else:
            return error_response(message="用户不存在", code=404)
    except Exception as e:
        logger.error(f"删除用户失败: {e}")
        return error_response(message="删除用户失败")

@user_bp.route('/search', methods=['GET'])
def search_users():
    """搜索用户"""
    try:
        name = request.args.get('name', '')
        if not name:
            return error_response(message="搜索关键词不能为空")
        
        users = user_service.search_users(name)
        schema = UserSchema(many=True)
        return success_response(data=schema.dump(users))
    except Exception as e:
        logger.error(f"搜索用户失败: {e}")
        return error_response(message="搜索用户失败")

@user_bp.route('/count', methods=['GET'])
def get_user_count():
    """获取用户总数"""
    try:
        count = user_service.get_user_count()
        return success_response(data={'count': count})
    except Exception as e:
        logger.error(f"获取用户总数失败: {e}")
        return error_response(message="获取用户总数失败")
