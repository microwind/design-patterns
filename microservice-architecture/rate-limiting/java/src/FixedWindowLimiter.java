package src;

public class FixedWindowLimiter {
    private final int limit;
    private int count;

    public FixedWindowLimiter(int limit) {
        this.limit = limit;
    }

    public boolean allow() {
        if (count >= limit) {
            return false;
        }
        count++;
        return true;
    }

    public void advanceWindow() {
        count = 0;
    }
}
