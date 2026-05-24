package order

import (
	orderModel "gin-ddd/internal/domain/model/order"
)

// toDO 将订单领域模型转换为数据对象,所有字段映射在编译期可见。
func toDO(o *orderModel.Order) *OrderDO {
	if o == nil {
		return nil
	}
	return &OrderDO{
		ID:          o.OrderID,
		OrderNo:     o.OrderNo,
		UserID:      o.UserID,
		TotalAmount: o.TotalAmount,
		Status:      string(o.Status),
		CreatedAt:   o.CreatedAt,
		UpdatedAt:   o.UpdatedAt,
	}
}

// toModel 将数据对象还原为领域模型,经聚合根 Restore 工厂构建,不触发领域事件。
func toModel(do *OrderDO) *orderModel.Order {
	if do == nil {
		return nil
	}
	return orderModel.Restore(
		do.ID,
		do.OrderNo,
		do.UserID,
		do.TotalAmount,
		orderModel.OrderStatus(do.Status),
		do.CreatedAt,
		do.UpdatedAt,
	)
}

// toModels 批量转换。
func toModels(dos []*OrderDO) []*orderModel.Order {
	if dos == nil {
		return nil
	}
	out := make([]*orderModel.Order, 0, len(dos))
	for _, do := range dos {
		out = append(out, toModel(do))
	}
	return out
}
