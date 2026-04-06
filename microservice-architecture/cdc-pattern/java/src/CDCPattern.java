package src;

import java.util.ArrayList;
import java.util.List;

public class CDCPattern {

    public static class ChangeRecord {
        private final String changeId;
        private final String aggregateId;
        private final String changeType;
        private boolean processed;

        public ChangeRecord(String changeId, String aggregateId, String changeType, boolean processed) {
            this.changeId = changeId;
            this.aggregateId = aggregateId;
            this.changeType = changeType;
            this.processed = processed;
        }

        public String getChangeId() {
            return changeId;
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }
    }

    public static class DataStore {
        private final List<ChangeRecord> changes = new ArrayList<>();

        public void createOrder(String orderId) {
            changes.add(new ChangeRecord("CHG-" + orderId, orderId, "order_created", false));
        }

        public void relayChanges(Broker broker) {
            for (ChangeRecord change : changes) {
                if (!change.isProcessed()) {
                    broker.publish(change.getChangeId());
                    change.setProcessed(true);
                }
            }
        }

        public List<ChangeRecord> getChanges() {
            return changes;
        }
    }

    public static class Broker {
        private final List<String> published = new ArrayList<>();

        public void publish(String changeId) {
            published.add(changeId);
        }

        public List<String> getPublished() {
            return published;
        }
    }
}
