from marshmallow import Schema, fields, validate


class UserSchema(Schema):
    """用户数据验证模式"""
    
    class Meta:
        fields = ('id', 'name', 'email', 'phone', 'created_at', 'updated_at')
    
    id = fields.Int(dump_only=True)
    name = fields.Str(required=True, validate=validate.Length(min=1, max=100))
    email = fields.Email(required=True, validate=validate.Length(max=100))
    phone = fields.Str(validate=validate.Length(max=20), allow_none=True)
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)


class CreateUserSchema(Schema):
    """创建用户数据验证模式"""
    name = fields.Str(required=True, validate=validate.Length(min=1, max=100))
    email = fields.Email(required=True, validate=validate.Length(max=100))
    phone = fields.Str(validate=validate.Length(max=20), allow_none=True)


class UpdateUserSchema(Schema):
    """更新用户数据验证模式"""
    name = fields.Str(validate=validate.Length(min=1, max=100))
    email = fields.Email(validate=validate.Length(max=100))
    phone = fields.Str(validate=validate.Length(max=20), allow_none=True)
