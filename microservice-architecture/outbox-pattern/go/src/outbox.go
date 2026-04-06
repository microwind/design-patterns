package src

type Order struct {
	OrderID string
	Status  string
}

type OutboxEvent struct {
	EventID     string
	AggregateID string
	EventType   string
	Status      string
}

type OutboxService struct {
	orders []Order
	outbox []OutboxEvent
}

type MemoryBroker struct {
	published []string
}

func NewOutboxService() *OutboxService {
	return &OutboxService{}
}

func NewMemoryBroker() *MemoryBroker {
	return &MemoryBroker{}
}

func (s *OutboxService) CreateOrder(orderID string) {
	s.orders = append(s.orders, Order{OrderID: orderID, Status: "CREATED"})
	s.outbox = append(s.outbox, OutboxEvent{
		EventID:     "EVT-" + orderID,
		AggregateID: orderID,
		EventType:   "order_created",
		Status:      "pending",
	})
}

func (s *OutboxService) RelayPending(broker *MemoryBroker) {
	for i := range s.outbox {
		if s.outbox[i].Status == "pending" {
			broker.published = append(broker.published, s.outbox[i].EventID)
			s.outbox[i].Status = "published"
		}
	}
}

func (s *OutboxService) Orders() []Order {
	return s.orders
}

func (s *OutboxService) Outbox() []OutboxEvent {
	return s.outbox
}

func (b *MemoryBroker) Published() []string {
	return b.published
}
