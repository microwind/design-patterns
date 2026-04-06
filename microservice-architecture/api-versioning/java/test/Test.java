package test;

import src.VersionedRouter;

import java.util.HashMap;
import java.util.Map;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        VersionedRouter router = new VersionedRouter("v1");
        router.register("v1", VersionedRouter::productHandlerV1);
        router.register("v2", VersionedRouter::productHandlerV2);

        VersionedRouter.Response defaultResponse = router.handle(new VersionedRouter.Request(
                "/products/P100",
                new HashMap<>()
        ));
        assertEquals("v1", defaultResponse.getVersion(), "default version");

        Map<String, String> v2Headers = new HashMap<>();
        v2Headers.put("X-API-Version", "2");
        VersionedRouter.Response v2Response = router.handle(new VersionedRouter.Request(
                "/products/P100",
                v2Headers
        ));
        assertEquals("v2", v2Response.getVersion(), "header selected version");

        Map<String, String> badHeaders = new HashMap<>();
        badHeaders.put("X-API-Version", "9");
        VersionedRouter.Response badResponse = router.handle(new VersionedRouter.Request(
                "/products/P100",
                badHeaders
        ));
        assertEquals(400, badResponse.getStatusCode(), "unsupported version should fail");

        System.out.println("api-versioning(java) tests passed");
    }
}
