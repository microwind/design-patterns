package test;

import src.Balancers;

import java.util.List;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        Balancers.RoundRobinBalancer roundRobin = new Balancers.RoundRobinBalancer(List.of(
                new Balancers.Backend("node-a", 1, 0),
                new Balancers.Backend("node-b", 1, 0),
                new Balancers.Backend("node-c", 1, 0)
        ));
        assertEquals("node-a", roundRobin.next().getBackendId(), "round robin first");
        assertEquals("node-b", roundRobin.next().getBackendId(), "round robin second");
        assertEquals("node-c", roundRobin.next().getBackendId(), "round robin third");

        Balancers.WeightedRoundRobinBalancer weighted = new Balancers.WeightedRoundRobinBalancer(List.of(
                new Balancers.Backend("node-a", 2, 0),
                new Balancers.Backend("node-b", 1, 0)
        ));
        int nodeACount = 0;
        int nodeBCount = 0;
        for (int i = 0; i < 6; i++) {
            String backendId = weighted.next().getBackendId();
            if ("node-a".equals(backendId)) {
                nodeACount++;
            } else if ("node-b".equals(backendId)) {
                nodeBCount++;
            }
        }
        assertEquals(4, nodeACount, "weighted count for node-a");
        assertEquals(2, nodeBCount, "weighted count for node-b");

        Balancers.LeastConnectionsBalancer leastConnections = new Balancers.LeastConnectionsBalancer(List.of(
                new Balancers.Backend("node-a", 1, 2),
                new Balancers.Backend("node-b", 1, 0),
                new Balancers.Backend("node-c", 1, 1)
        ));
        assertEquals("node-b", leastConnections.acquire().getBackendId(), "least connections should pick node-b");
        leastConnections.release("node-b");

        System.out.println("load-balancing(java) tests passed");
    }
}
