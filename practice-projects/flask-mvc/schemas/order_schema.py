from marshmallow import Schema, fields, validate


class OrderSchema(Schema):
    """订单数据验证模式"""
    
    class Meta:
        fields = ('id', 'user_id', 'order_no', 'total_amount', 'status', 'created_at', 'updated_at')
    
    id = fields.Int(dump_only=True)
    user_id = fields.Int(required=True, validate=validate.Range(min=1))
    order_no = fields.Str(required=True, validate=validate.Length(min=5, max=50))
    total_amount = fields.Decimal(required=True, places=2, validate=validate.Range(min=0))
    status = fields.Str(dump_only=True, validate=validate.OneOf(['PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED']))
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)


class CreateOrderSchema(Schema):
    """创建订单数据验证模式"""
    user_id = fields.Int(required=True, validate=validate.Range(min=1))
    order_no = fields.Str(required=True, validate=validate.Length(min=5, max=50))
    total_amount = fields.Decimal(required=True, places=2, validate=validate.Range(min=0))


class UpdateOrderSchema(Schema):
    """更新订单数据验证模式"""
    status = fields.Str(validate=validate.OneOf(['PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED']))