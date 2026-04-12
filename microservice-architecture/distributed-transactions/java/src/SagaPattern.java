package src;

/**
 * SagaPattern - 分布式事务 Saga 模式的 Java 实现
 *
 * 【设计模式】
 *   - 命令模式（Command Pattern）：每个步骤（reserve/charge）和补偿（release）是独立的命令。
 *   - 责任链模式（Chain of Responsibility）：正向步骤按链式顺序执行。
 *   - 状态模式（State Pattern）：订单状态 PENDING → COMPLETED / CANCELLED。
 *
 * 【架构思想】
 *   Saga 将跨服务事务拆分为有序步骤 + 补偿动作。失败时逆序补偿，
 *   实现最终一致性而非强一致性。
 *
 * 【开源对比】
 *   - Seata Saga：阿里巴巴的分布式事务框架，支持状态机引擎编排 Saga
 *   - Temporal：强类型工作流引擎，支持 Saga + 补偿 + 超时
 *   - Axon Framework：CQRS + Saga 支持
 *   本示例用同步方法调用模拟 Saga 编排，省略了持久化和超时。
 */
public class SagaPattern {

    /**
     * SagaOrder - Saga 订单实体
     * 状态：PENDING → COMPLETED（全部成功）或 CANCELLED（步骤失败+补偿）
     */
    public static class SagaOrder {
        private final String orderId;
        private String status;  // PENDING / COMPLETED / CANCELLED

        public SagaOrder(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * InventoryService - 库存服务
     * 提供正向操作 reserve（预占）和补偿操作 release（释放）。
     */
    public static class InventoryService {
        private int bookStock;

        public InventoryService(int stock) {
            this.bookStock = stock;
        }

        /** 正向步骤：预占库存 */
        public boolean reserve(String sku, int quantity) {
            if (!"SKU-BOOK".equals(sku) || quantity <= 0 || bookStock < quantity) {
                return false;
            }
            bookStock -= quantity;
            return true;
        }

        /** 补偿动作：释放已预占的库存 */
        public void release(String sku, int quantity) {
            if ("SKU-BOOK".equals(sku) && quantity > 0) {
                bookStock += quantity;
            }
        }

        public int getBookStock() { return bookStock; }
    }

    /**
     * PaymentService - 支付服务
     * fail 标志模拟支付成功/失败场景。
     */
    public static class PaymentService {
        private final boolean fail;

        public PaymentService(boolean fail) {
            this.fail = fail;
        }

        /** 正向步骤：扣款 */
        public boolean charge(String orderId) {
            return !fail;
        }
    }

    /**
     * SagaCoordinator - Saga 协调者（编排式）
     *
     * 【设计模式】命令模式 + 责任链：按序执行步骤，失败时逆序补偿。
     *
     * 执行流程：
     *   1. 库存预占 → 失败则直接 CANCELLED
     *   2. 支付扣款 → 失败则补偿（释放库存）→ CANCELLED
     *   3. 全部成功 → COMPLETED
     */
    public static class SagaCoordinator {
        private final InventoryService inventory;
        private final PaymentService payment;

        public SagaCoordinator(int stock, boolean paymentFails) {
            this.inventory = new InventoryService(stock);
            this.payment = new PaymentService(paymentFails);
        }

        /**
         * 执行 Saga 事务。
         * @return 最终订单状态：COMPLETED 或 CANCELLED
         */
        public SagaOrder execute(String orderId, String sku, int quantity) {
            SagaOrder order = new SagaOrder(orderId, "PENDING");

            // 步骤1：库存预占
            if (!inventory.reserve(sku, quantity)) {
                order.setStatus("CANCELLED");
                return order;
            }

            // 步骤2：支付扣款
            if (!payment.charge(orderId)) {
                // 支付失败 → 补偿：释放库存
                inventory.release(sku, quantity);
                order.setStatus("CANCELLED");
                return order;
            }

            // 全部成功
            order.setStatus("COMPLETED");
            return order;
        }

        public InventoryService getInventory() { return inventory; }
    }
}
