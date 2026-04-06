import { ConfigCenter, ConfigClient } from "../src/configuration_center";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const center = new ConfigCenter();
center.put({
  serviceName: "order-service",
  environment: "prod",
  version: 1,
  dbHost: "db.prod.internal",
  timeoutMs: 300,
  featureOrderAudit: false
});

const client = new ConfigClient(center, "order-service", "prod");
const loaded = client.load();
assertEqual(1, loaded?.version ?? 0, "initial version");
assertEqual(300, loaded?.timeoutMs ?? 0, "initial timeout");

center.put({
  serviceName: "order-service",
  environment: "prod",
  version: 2,
  dbHost: "db.prod.internal",
  timeoutMs: 500,
  featureOrderAudit: true
});

const refreshed = client.refresh();
assertEqual(2, refreshed?.version ?? 0, "refreshed version");
assertEqual(true, refreshed?.featureOrderAudit ?? false, "feature flag should refresh");

console.log("configuration-center(ts) tests passed");
