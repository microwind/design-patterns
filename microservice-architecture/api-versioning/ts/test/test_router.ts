import { productHandlerV1, productHandlerV2, Request, VersionedRouter } from "../src/router";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const router = new VersionedRouter("v1");
router.register("v1", productHandlerV1);
router.register("v2", productHandlerV2);

const defaultRequest: Request = { path: "/products/P100", headers: {} };
assertEqual("v1", router.handle(defaultRequest).version, "default version should be v1");

const headerRequest: Request = { path: "/products/P100", headers: { "X-API-Version": "2" } };
assertEqual("v2", router.handle(headerRequest).version, "header should select v2");

const unsupportedRequest: Request = { path: "/products/P100", headers: { "X-API-Version": "9" } };
assertEqual(400, router.handle(unsupportedRequest).statusCode, "unsupported version should fail");

console.log("api-versioning(ts) tests passed");
