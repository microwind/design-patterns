import {
  Backend,
  LeastConnectionsBalancer,
  RoundRobinBalancer,
  WeightedRoundRobinBalancer
} from "../src/balancer";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const roundRobin = new RoundRobinBalancer([
  { backendId: "node-a" },
  { backendId: "node-b" },
  { backendId: "node-c" }
]);
assertEqual("node-a", roundRobin.next().backendId, "first round robin backend");
assertEqual("node-b", roundRobin.next().backendId, "second round robin backend");
assertEqual("node-c", roundRobin.next().backendId, "third round robin backend");

const weighted = new WeightedRoundRobinBalancer([
  { backendId: "node-a", weight: 2 },
  { backendId: "node-b", weight: 1 }
]);
const counts: Record<string, number> = {};
for (let i = 0; i < 6; i++) {
  const backendId = weighted.next().backendId;
  counts[backendId] = (counts[backendId] ?? 0) + 1;
}
assertEqual(4, counts["node-a"], "node-a should receive 4 selections");
assertEqual(2, counts["node-b"], "node-b should receive 2 selections");

const leastConnections = new LeastConnectionsBalancer([
  { backendId: "node-a", activeConnections: 2 },
  { backendId: "node-b", activeConnections: 0 },
  { backendId: "node-c", activeConnections: 1 }
]);
assertEqual("node-b", leastConnections.acquire().backendId, "least busy backend should be selected");
leastConnections.release("node-b");

console.log("load-balancing(ts) tests passed");
