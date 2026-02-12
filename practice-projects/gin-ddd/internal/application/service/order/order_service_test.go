package order

import (
	"context"
	"testing"

	"gin-ddd/internal/domain/event"
	"gin-ddd/internal/domain/model/order"
)

// MockOrderRepository 模拟的订单仓储
type MockOrderRepository struct {
	orders map[int64]*order.Order
	nextID int64
}

// MockEventPublisher 模拟的事件发布器
type MockEventPublisher struct {
	publishedEvents []event.DomainEvent
}

// Create 创建订单
func (m *MockOrderRepository) Create(ctx context.Context, o *order.Order) error {
	m.nextID++
	o.OrderID = m.nextID
	m.orders[o.OrderID] = o
	return nil
}

// Update 更新订单
func (m *MockOrderRepository) Update(ctx context.Context, o *order.Order) error {
	m.orders[o.OrderID] = o
	return nil
}

// Delete 删除订单
func (m *MockOrderRepository) Delete(ctx context.Context, id int64) error {
	delete(m.orders, id)
	return nil
}

// FindByID 根据ID查询订单
func (m *MockOrderRepository) FindByID(ctx context.Context, id int64) (*order.Order, error) {
	return m.orders[id], nil
}

// FindByOrderNo 根据订单号查询订单
func (m *MockOrderRepository) FindByOrderNo(ctx context.Context, orderNo string) (*order.Order, error) {
	for _, o := range m.orders {
		if o.OrderNo == orderNo {
			return o, nil
		}
	}
	return nil, nil
}

// FindByUserID 根据用户ID查询订单
func (m *MockOrderRepository) FindByUserID(ctx context.Context, userID int64) ([]*order.Order, error) {
	var orders []*order.Order
	for _, o := range m.orders {
		if o.UserID == userID {
			orders = append(orders, o)
		}
	}
	return orders, nil
}

// FindAll 查询所有订单
func (m *MockOrderRepository) FindAll(ctx context.Context) ([]*order.Order, error) {
	var orders []*order.Order
	for _, o := range m.orders {
		orders = append(orders, o)
	}
	return orders, nil
}

// FindByStatus 根据状态查询订单列表
func (m *MockOrderRepository) FindByStatus(ctx context.Context, status order.OrderStatus) ([]*order.Order, error) {
	var orders []*order.Order
	for _, o := range m.orders {
		if o.Status == status {
			orders = append(orders, o)
		}
	}
	return orders, nil
}

// Publish 发布事件
func (m *MockEventPublisher) Publish(ctx context.Context, topic string, e event.DomainEvent) error {
	m.publishedEvents = append(m.publishedEvents, e)
	return nil
}

// Close 关闭发布器
func (m *MockEventPublisher) Close() error {
	return nil
}

func TestCreateOrderPublishesEvent(t *testing.T) {
	mockRepo := &MockOrderRepository{orders: make(map[int64]*order.Order)}
	mockPublisher := &MockEventPublisher{}
	service := NewOrderService(mockRepo, mockPublisher)

	ctx := context.Background()
	dto, err := service.CreateOrder(ctx, 1, 100.00)

	if err != nil {
		t.Fatalf("创建订单失败: %v", err)
	}

	if dto == nil {
		t.Fatal("期望获取订单DTO，但返回了nil")
	}

	// 检查事件是否已发布
	if len(mockPublisher.publishedEvents) != 1 {
		t.Errorf("期望发布1个事件，但发布了 %d 个", len(mockPublisher.publishedEvents))
	}

	// 检查事件类型
	if len(mockPublisher.publishedEvents) > 0 {
		publishedEvent := mockPublisher.publishedEvents[0]
		if publishedEvent.EventType() != event.OrderCreatedEvent {
			t.Errorf("期望事件类型为 %s，但获取到 %s", event.OrderCreatedEvent, publishedEvent.EventType())
		}
	}
}

func TestCreateOrderEventContainsOrderInfo(t *testing.T) {
	mockRepo := &MockOrderRepository{orders: make(map[int64]*order.Order)}
	mockPublisher := &MockEventPublisher{}
	service := NewOrderService(mockRepo, mockPublisher)

	ctx := context.Background()
	userID := int64(123)
	amount := 99.99

	_, err := service.CreateOrder(ctx, userID, amount)
	if err != nil {
		t.Fatalf("创建订单失败: %v", err)
	}

	if len(mockPublisher.publishedEvents) == 0 {
		t.Fatal("期望发布事件，但没有事件被发布")
	}

	publishedEvent := mockPublisher.publishedEvents[0].(*event.OrderEvent)

	if publishedEvent.UserID != userID {
		t.Errorf("期望用户ID为 %d，但获取到 %d", userID, publishedEvent.UserID)
	}

	if publishedEvent.TotalAmount != amount {
		t.Errorf("期望订单金额为 %f，但获取到 %f", amount, publishedEvent.TotalAmount)
	}

	if publishedEvent.Status != "PENDING" {
		t.Errorf("期望订单状态为 PENDING，但获取到 %s", publishedEvent.Status)
	}
}
