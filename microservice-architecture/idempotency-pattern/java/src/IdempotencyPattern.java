package src;

import java.util.HashMap;
import java.util.Map;

/**
 * IdempotencyPattern - 幂等模式的 Java 实现
 *
 * 【设计模式】
 *   - 备忘录模式（Memento Pattern）：首次执行结果被存储，后续重复请求返回备忘结果。
 *   - 代理模式（Proxy Pattern）：幂等层包裹在业务逻辑之外，透明拦截重复请求。
 *
 * 【架构思想】
 *   分布式环境中，网络重试/消息重投/用户重复提交会导致同一请求被执行多次。
 *   幂等模式通过 idempotencyKey 将重复请求折叠为同一结果。
 *
 * 【开源对比】
 *   - Stripe API：通过 Idempotency-Key HTTP Header 实现幂等，结果存储在 Redis 中
 *   - Spring Retry + Redis：通过 Redis SETNX 实现幂等锁
 *   本示例用内存 Map 简化，省略了 TTL 过期和并发控制。
 */
public class IdempotencyPattern {

    /**
     * OrderResponse - 订单响应（值对象）
     *
     * replayed 字段区分首次执行和重复请求：
     *   false = 首次执行的真实结果
     *   true  = 从存储中返回的重放结果
     */
    public static class OrderResponse {
        private final String orderId;
        private final String sku;
        private final int quantity;
        /** 订单状态：CREATED / CONFLICT */
        private final String status;
        /** 是否为重放结果 */
        private final boolean replayed;

        public OrderResponse(String orderId, String sku, int quantity, String status, boolean replayed) {
            this.orderId = orderId;
            this.sku = sku;
            this.quantity = quantity;
            this.status = status;
            this.replayed = replayed;
        }

        public String getOrderId() { return orderId; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public String getStatus() { return status; }
        public boolean isReplayed() { return replayed; }
    }

    /**
     * StoredResult - 存储的幂等结果（内部类）
     *
     * fingerprint 用于检测同一幂等键但参数不同的冲突请求。
     */
    private static class StoredResult {
        /** 请求指纹（参数拼接） */
        private final String fingerprint;
        /** 首次执行的响应结果 */
        private final OrderResponse response;

        private StoredResult(String fingerprint, OrderResponse response) {
            this.fingerprint = fingerprint;
            this.response = response;
        }
    }

    /**
     * IdempotencyOrderService - 带幂等保护的订单服务
     *
     * 三条路径：
     *   1. 首次请求 → 执行业务 → 存储结果 → 返回 CREATED
     *   2. 重复请求 + 指纹匹配 → 返回存储结果（replayed=true）
     *   3. 重复请求 + 指纹不匹配 → 返回 CONFLICT
     */
    public static class IdempotencyOrderService {
        /** 幂等存储：idempotencyKey → StoredResult */
        private final Map<String, StoredResult> store = new HashMap<>();

        /**
         * 创建订单（带幂等保护）。
         *
         * @param idempotencyKey 幂等键（调用方生成，唯一标识一次业务意图）
         * @param orderId        订单ID
         * @param sku            商品SKU
         * @param quantity       数量
         * @return 订单响应
         */
        public OrderResponse createOrder(String idempotencyKey, String orderId, String sku, int quantity) {
            // 计算请求指纹，用于冲突检测
            String fingerprint = orderId + "|" + sku + "|" + quantity;
            StoredResult existing = store.get(idempotencyKey);
            if (existing != null) {
                // 同一幂等键但参数不同 → 冲突
                if (!existing.fingerprint.equals(fingerprint)) {
                    return new OrderResponse(orderId, sku, quantity, "CONFLICT", false);
                }
                // 同一幂等键且参数相同 → 返回存储的结果
                return new OrderResponse(
                        existing.response.getOrderId(),
                        existing.response.getSku(),
                        existing.response.getQuantity(),
                        existing.response.getStatus(),
                        true
                );
            }

            // 首次请求 → 执行业务逻辑并存储结果
            OrderResponse response = new OrderResponse(orderId, sku, quantity, "CREATED", false);
            store.put(idempotencyKey, new StoredResult(fingerprint, response));
            return response;
        }
    }
}
