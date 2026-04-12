package src

// Order 表示订单实体（值对象）。
//
// 【设计模式】值对象模式：创建后状态不再改变。
// Status 为 "CREATED"（库存充足，订单创建成功）或 "REJECTED"（库存不足，被拒绝）。
type Order struct {
	OrderID  string // 订单ID
	Sku      string // 商品SKU编码
	Quantity int    // 订购数量
	Status   string // 订单状态：CREATED / REJECTED
}
