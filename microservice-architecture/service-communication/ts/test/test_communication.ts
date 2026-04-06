import {
  AsyncOrderService,
  EventBus,
  InventoryService,
  OrderStore,
  PaymentService,
  registerAsyncWorkflow,
  SynchronousOrderService
} from "../src/communication";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const syncInventory = new InventoryService({ "SKU-BOOK": 5 });
const syncPayment = new PaymentService();
const syncService = new SynchronousOrderService(syncInventory, syncPayment);
assertEqual("CREATED", syncService.placeOrder("ORD-1001", "SKU-BOOK", 2).status, "sync order should succeed");

const bus = new EventBus();
const store = new OrderStore();
const asyncInventory = new InventoryService({ "SKU-BOOK": 5 });
const asyncPayment = new PaymentService();
registerAsyncWorkflow(bus, store, asyncInventory, asyncPayment);
const asyncService = new AsyncOrderService(bus, store);
assertEqual("PENDING", asyncService.placeOrder("ORD-2001", "SKU-BOOK", 2).status, "async order should start pending");
bus.drain();
assertEqual("CREATED", store.get("ORD-2001").status, "async order should finish created");

const failingBus = new EventBus();
const failingStore = new OrderStore();
registerAsyncWorkflow(
  failingBus,
  failingStore,
  new InventoryService({ "SKU-BOOK": 5 }),
  new PaymentService(["ORD-2002"])
);
new AsyncOrderService(failingBus, failingStore).placeOrder("ORD-2002", "SKU-BOOK", 1);
failingBus.drain();
assertEqual("PAYMENT_FAILED", failingStore.get("ORD-2002").status, "async failure should be captured");

console.log("service-communication(ts) tests passed");
