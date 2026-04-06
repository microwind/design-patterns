package src;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class CommunicationModels {

    public static class Order {
        private final String orderId;
        private final String sku;
        private final int quantity;
        private final String status;

        public Order(String orderId, String sku, int quantity, String status) {
            this.orderId = orderId;
            this.sku = sku;
            this.quantity = quantity;
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class InventoryService {
        private final Map<String, Integer> stock = new HashMap<>();

        public InventoryService(Map<String, Integer> stock) {
            this.stock.putAll(stock);
        }

        public boolean reserve(String sku, int quantity) {
            int available = stock.getOrDefault(sku, 0);
            if (quantity <= 0 || available < quantity) {
                return false;
            }
            stock.put(sku, available - quantity);
            return true;
        }
    }

    public static class PaymentService {
        private final Set<String> failOrderIds = new HashSet<>();

        public PaymentService(List<String> failOrderIds) {
            this.failOrderIds.addAll(failOrderIds);
        }

        public boolean charge(String orderId) {
            return !failOrderIds.contains(orderId);
        }
    }

    public static class SynchronousOrderService {
        private final InventoryService inventory;
        private final PaymentService payment;

        public SynchronousOrderService(InventoryService inventory, PaymentService payment) {
            this.inventory = inventory;
            this.payment = payment;
        }

        public Order placeOrder(String orderId, String sku, int quantity) {
            if (!inventory.reserve(sku, quantity)) {
                return new Order(orderId, sku, quantity, "REJECTED");
            }
            if (!payment.charge(orderId)) {
                return new Order(orderId, sku, quantity, "PAYMENT_FAILED");
            }
            return new Order(orderId, sku, quantity, "CREATED");
        }
    }

    public static class Event {
        private final String name;
        private final String orderId;
        private final String sku;
        private final int quantity;

        public Event(String name, String orderId, String sku, int quantity) {
            this.name = name;
            this.orderId = orderId;
            this.sku = sku;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static class EventBus {
        private final Map<String, List<Consumer<Event>>> subscribers = new HashMap<>();
        private final Queue<Event> queue = new ArrayDeque<>();

        public void subscribe(String eventName, Consumer<Event> handler) {
            subscribers.computeIfAbsent(eventName, ignored -> new ArrayList<>()).add(handler);
        }

        public void publish(Event event) {
            queue.offer(event);
        }

        public void drain() {
            while (!queue.isEmpty()) {
                Event event = queue.poll();
                for (Consumer<Event> handler : subscribers.getOrDefault(event.getName(), List.of())) {
                    handler.accept(event);
                }
            }
        }
    }

    public static class OrderStore {
        private final Map<String, Order> orders = new HashMap<>();

        public void save(Order order) {
            orders.put(order.getOrderId(), order);
        }

        public void updateStatus(String orderId, String status) {
            Order order = orders.get(orderId);
            orders.put(orderId, new Order(order.getOrderId(), order.getSku(), order.getQuantity(), status));
        }

        public Order get(String orderId) {
            return orders.get(orderId);
        }
    }

    public static class AsyncOrderService {
        private final EventBus bus;
        private final OrderStore store;

        public AsyncOrderService(EventBus bus, OrderStore store) {
            this.bus = bus;
            this.store = store;
        }

        public Order placeOrder(String orderId, String sku, int quantity) {
            Order order = new Order(orderId, sku, quantity, "PENDING");
            store.save(order);
            bus.publish(new Event("order_placed", orderId, sku, quantity));
            return order;
        }
    }

    public static void registerAsyncWorkflow(
            EventBus bus,
            OrderStore store,
            InventoryService inventory,
            PaymentService payment
    ) {
        bus.subscribe("order_placed", event -> {
            if (!inventory.reserve(event.getSku(), event.getQuantity())) {
                store.updateStatus(event.getOrderId(), "REJECTED");
                return;
            }
            if (!payment.charge(event.getOrderId())) {
                store.updateStatus(event.getOrderId(), "PAYMENT_FAILED");
                return;
            }
            store.updateStatus(event.getOrderId(), "CREATED");
        });
    }
}
