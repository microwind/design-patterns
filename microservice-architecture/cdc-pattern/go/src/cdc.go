package src

type ChangeRecord struct {
	ChangeID    string
	AggregateID string
	ChangeType  string
	Processed   bool
}

type DataStore struct {
	changes []ChangeRecord
}

type Broker struct {
	published []string
}

func NewDataStore() *DataStore {
	return &DataStore{}
}

func NewBroker() *Broker {
	return &Broker{}
}

func (d *DataStore) CreateOrder(orderID string) {
	d.changes = append(d.changes, ChangeRecord{
		ChangeID:    "CHG-" + orderID,
		AggregateID: orderID,
		ChangeType:  "order_created",
		Processed:   false,
	})
}

func (d *DataStore) RelayChanges(b *Broker) {
	for i := range d.changes {
		if !d.changes[i].Processed {
			b.published = append(b.published, d.changes[i].ChangeID)
			d.changes[i].Processed = true
		}
	}
}

func (d *DataStore) Changes() []ChangeRecord {
	return d.changes
}

func (b *Broker) Published() []string {
	return b.published
}
