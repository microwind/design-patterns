package test;

import src.CommunicationModels;

import java.util.List;
import java.util.Map;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        CommunicationModels.SynchronousOrderService syncService = new CommunicationModels.SynchronousOrderService(
                new CommunicationModels.InventoryService(Map.of("SKU-BOOK", 5)),
                new CommunicationModels.PaymentService(List.of())
        );
        assertEquals("CREATED", syncService.placeOrder("ORD-1001", "SKU-BOOK", 2).getStatus(), "sync order");

        CommunicationModels.EventBus bus = new CommunicationModels.EventBus();
        CommunicationModels.OrderStore store = new CommunicationModels.OrderStore();
        CommunicationModels.registerAsyncWorkflow(
                bus,
                store,
                new CommunicationModels.InventoryService(Map.of("SKU-BOOK", 5)),
                new CommunicationModels.PaymentService(List.of())
        );
        CommunicationModels.AsyncOrderService asyncService = new CommunicationModels.AsyncOrderService(bus, store);
        assertEquals("PENDING", asyncService.placeOrder("ORD-2001", "SKU-BOOK", 2).getStatus(), "async pending");
        bus.drain();
        assertEquals("CREATED", store.get("ORD-2001").getStatus(), "async created");

        CommunicationModels.EventBus failingBus = new CommunicationModels.EventBus();
        CommunicationModels.OrderStore failingStore = new CommunicationModels.OrderStore();
        CommunicationModels.registerAsyncWorkflow(
                failingBus,
                failingStore,
                new CommunicationModels.InventoryService(Map.of("SKU-BOOK", 5)),
                new CommunicationModels.PaymentService(List.of("ORD-2002"))
        );
        new CommunicationModels.AsyncOrderService(failingBus, failingStore)
                .placeOrder("ORD-2002", "SKU-BOOK", 1);
        failingBus.drain();
        assertEquals("PAYMENT_FAILED", failingStore.get("ORD-2002").getStatus(), "async failure");

        System.out.println("service-communication(java) tests passed");
    }
}
