# apps/order/schemas.py
from marshmallow import Schema, fields, validate

class OrderSchema(Schema):
    order_no = fields.Str(required=True, validate=validate.Length(min=5, max=50))
    user_id = fields.Int(required=True)
    amount = fields.Decimal(required=True, places=2)
    status = fields.Str(validate=validate.OneOf(['pending', 'completed', 'cancelled']))