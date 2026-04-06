package test;

import src.FeatureFlagPattern;

import java.util.Map;

public class Test {
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        FeatureFlagPattern.FeatureFlagService service = new FeatureFlagPattern.FeatureFlagService();
        service.set("new-checkout", new FeatureFlagPattern.FeatureFlag(false, Map.of("user-1", true)));
        assertEquals(true, service.enabled("new-checkout", "user-1"), "allowlist");
        assertEquals(false, service.enabled("new-checkout", "user-2"), "default disabled");
        System.out.println("feature-flag(java) tests passed");
    }
}
