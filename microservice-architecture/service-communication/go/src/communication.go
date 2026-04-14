// Package src 实现了服务间通信模式（Service Communication Pattern）的核心逻辑。
//
// 本模块演示微服务架构中两种核心通信方式：同步通信（请求-响应）和异步通信（事件驱动）。
//
// 【设计模式】
//   - 外观模式（Facade Pattern）：SynchronousOrderService 将 InventoryService 和
//     PaymentService 的调用封装为统一的 PlaceOrder 接口。
//   - 观察者模式（Observer Pattern）：EventBus 实现发布-订阅机制，
//     AsyncOrderService 发布事件，下游通过 Subscribe 注册处理函数。
//   - 中介者模式（Mediator Pattern）：EventBus 充当中介者，协调多服务间交互。
//
// 【架构思想】
//   同步通信简单直观但耦合度高；异步通信通过事件总线解耦，支持独立扩展和故障隔离。
//
// 【开源对比】
//   - 同步：gRPC-Go、go-micro（RPC 框架）
//   - 异步：NATS、Sarama（Kafka 客户端）、Watermill（事件驱动框架）
//   本示例省略了网络传输和序列化，聚焦于同步/异步两种编排模式的对比。
package src

type Order struct {
	ID       string
	SKU      string
	Quantity int
	Status   string
}

type InventoryService struct {
	stock map[string]int
}

func NewInventoryService(stock map[string]int) *InventoryService {
	cloned := map[string]int{}
	for sku, quantity := range stock {
		cloned[sku] = quantity
	}
	return &InventoryService{stock: cloned}
}

func (s *InventoryService) Reserve(sku string, quantity int) bool {
	available := s.stock[sku]
	if quantity <= 0 || available < quantity {
		return false
	}
	s.stock[sku] -= quantity
	return true
}

type PaymentService struct {
	failOrders map[string]bool
}

func NewPaymentService(failOrderIDs []string) *PaymentService {
	failOrders := map[string]bool{}
	for _, orderID := range failOrderIDs {
		failOrders[orderID] = true
	}
	return &PaymentService{failOrders: failOrders}
}

func (s *PaymentService) Charge(orderID string) bool {
	return !s.failOrders[orderID]
}

type SynchronousOrderService struct {
	inventory *InventoryService
	payment   *PaymentService
}

func NewSynchronousOrderService(inventory *InventoryService, payment *PaymentService) *SynchronousOrderService {
	return &SynchronousOrderService{inventory: inventory, payment: payment}
}

func (s *SynchronousOrderService) PlaceOrder(orderID string, sku string, quantity int) Order {
	if !s.inventory.Reserve(sku, quantity) {
		return Order{ID: orderID, SKU: sku, Quantity: quantity, Status: "REJECTED"}
	}

	if !s.payment.Charge(orderID) {
		return Order{ID: orderID, SKU: sku, Quantity: quantity, Status: "PAYMENT_FAILED"}
	}

	return Order{ID: orderID, SKU: sku, Quantity: quantity, Status: "CREATED"}
}

type Event struct {
	Name     string
	OrderID  string
	SKU      string
	Quantity int
}

type EventBus struct {
	subscribers map[string][]func(Event)
	queue       []Event
}

func NewEventBus() *EventBus {
	return &EventBus{
		subscribers: map[string][]func(Event){},
		queue:       []Event{},
	}
}

func (b *EventBus) Subscribe(eventName string, handler func(Event)) {
	b.subscribers[eventName] = append(b.subscribers[eventName], handler)
}

func (b *EventBus) Publish(event Event) {
	b.queue = append(b.queue, event)
}

func (b *EventBus) Drain() {
	for len(b.queue) > 0 {
		event := b.queue[0]
		b.queue = b.queue[1:]
		for _, subscriber := range b.subscribers[event.Name] {
			subscriber(event)
		}
	}
}

type OrderStore struct {
	orders map[string]Order
}

func NewOrderStore() *OrderStore {
	return &OrderStore{orders: map[string]Order{}}
}

func (s *OrderStore) Save(order Order) {
	s.orders[order.ID] = order
}

func (s *OrderStore) UpdateStatus(orderID string, status string) {
	order := s.orders[orderID]
	order.Status = status
	s.orders[orderID] = order
}

func (s *OrderStore) Get(orderID string) Order {
	return s.orders[orderID]
}

type AsyncOrderService struct {
	bus   *EventBus
	store *OrderStore
}

func NewAsyncOrderService(bus *EventBus, store *OrderStore) *AsyncOrderService {
	return &AsyncOrderService{bus: bus, store: store}
}

func (s *AsyncOrderService) PlaceOrder(orderID string, sku string, quantity int) Order {
	order := Order{ID: orderID, SKU: sku, Quantity: quantity, Status: "PENDING"}
	s.store.Save(order)
	s.bus.Publish(Event{Name: "order_placed", OrderID: orderID, SKU: sku, Quantity: quantity})
	return order
}

func RegisterAsyncWorkflow(bus *EventBus, store *OrderStore, inventory *InventoryService, payment *PaymentService) {
	bus.Subscribe("order_placed", func(event Event) {
		if !inventory.Reserve(event.SKU, event.Quantity) {
			store.UpdateStatus(event.OrderID, "REJECTED")
			return
		}

		if !payment.Charge(event.OrderID) {
			store.UpdateStatus(event.OrderID, "PAYMENT_FAILED")
			return
		}

		store.UpdateStatus(event.OrderID, "CREATED")
	})
}
