package src;

import java.util.ArrayList;
import java.util.List;

public class OutboxPattern {

    public static class Order {
        private final String orderId;
        private final String status;

        public Order(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class OutboxEvent {
        private final String eventId;
        private final String aggregateId;
        private final String eventType;
        private String status;

        public OutboxEvent(String eventId, String aggregateId, String eventType, String status) {
            this.eventId = eventId;
            this.aggregateId = aggregateId;
            this.eventType = eventType;
            this.status = status;
        }

        public String getEventId() {
            return eventId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class MemoryBroker {
        private final List<String> published = new ArrayList<>();

        public void publish(String eventId) {
            published.add(eventId);
        }

        public List<String> getPublished() {
            return published;
        }
    }

    public static class OutboxService {
        private final List<Order> orders = new ArrayList<>();
        private final List<OutboxEvent> outbox = new ArrayList<>();

        public void createOrder(String orderId) {
            orders.add(new Order(orderId, "CREATED"));
            outbox.add(new OutboxEvent("EVT-" + orderId, orderId, "order_created", "pending"));
        }

        public void relayPending(MemoryBroker broker) {
            for (OutboxEvent event : outbox) {
                if ("pending".equals(event.getStatus())) {
                    broker.publish(event.getEventId());
                    event.setStatus("published");
                }
            }
        }

        public List<Order> getOrders() {
            return orders;
        }

        public List<OutboxEvent> getOutbox() {
            return outbox;
        }
    }
}
