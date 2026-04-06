import {
  APIGateway,
  inventoryServiceHandler,
  orderServiceHandler,
  Request,
  requireUserHeader
} from "../src/gateway";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const gateway = new APIGateway();
gateway.use(requireUserHeader("/api/orders", "X-User"));
gateway.register("/api/orders", orderServiceHandler());
gateway.register("/api/inventory", inventoryServiceHandler());

const secured: Request = {
  method: "GET",
  path: "/api/orders/ORD-1001",
  headers: { "X-User": "jarry", "X-Correlation-ID": "trace-1001" }
};
const securedResponse = gateway.handle(secured);
assertEqual(200, securedResponse.statusCode, "secured route should succeed");
assertEqual("trace-1001", securedResponse.headers["X-Correlation-ID"], "correlation id should be preserved");
assertEqual("order-service", securedResponse.headers["X-Upstream-Service"], "order route should go upstream");

const unauthorized = gateway.handle({ method: "GET", path: "/api/orders/ORD-1002", headers: {} });
assertEqual(401, unauthorized.statusCode, "missing user should be rejected");

const inventory = gateway.handle({ method: "GET", path: "/api/inventory/SKU-BOOK", headers: {} });
assertEqual(200, inventory.statusCode, "inventory route should be public");
assertEqual("inventory-service", inventory.headers["X-Upstream-Service"], "inventory route should go upstream");

const missing = gateway.handle({ method: "GET", path: "/api/unknown", headers: {} });
assertEqual(404, missing.statusCode, "unknown route should return not found");

console.log("api-gateway(ts) tests passed");
