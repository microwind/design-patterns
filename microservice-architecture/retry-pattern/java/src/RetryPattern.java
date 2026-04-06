package src;

public class RetryPattern {

    public static class ScriptedOperation {
        private final int failuresBeforeSuccess;
        private int attempts;

        public ScriptedOperation(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        public boolean call() {
            attempts++;
            return attempts > failuresBeforeSuccess;
        }
    }

    public record RetryResult(boolean ok, int attempts) {}

    public static RetryResult retry(int maxAttempts, java.util.function.BooleanSupplier operation) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            if (operation.getAsBoolean()) {
                return new RetryResult(true, attempt);
            }
        }
        return new RetryResult(false, maxAttempts);
    }
}
