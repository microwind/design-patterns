package src;

import java.util.HashMap;
import java.util.Map;

/**
 * InventoryService - 本地库存服务实现（阶段1）
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：作为 InventoryClient 接口的本地实现策略，
 *     直接操作内存中的库存数据。
 *   - 适配器模式（Adapter Pattern）：将内存 Map 操作适配为统一的 reserve 接口。
 *
 * 【架构思想】
 *   阶段1 的库存服务运行在同一进程内，代表单体架构的调用方式。
 *   当演进到阶段2 时，同一个接口由 HttpInventoryClient 实现，
 *   OrderService 无需修改任何代码即可切换到远程调用。
 *
 * 【开源对比】
 *   实际工程中库存服务是独立部署的微服务，数据存储在数据库中（如 MySQL/Redis），
 *   库存预留通常需要分布式锁或乐观锁保证并发安全。
 *   本示例用内存 Map 简化，聚焦于服务拆分和契约调用的本质。
 */
public class InventoryService implements InventoryClient {

    /** 内存库存表：SKU -> 可用数量 */
    private final Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        // 初始化测试库存数据
        stock.put("SKU-BOOK", 10);
        stock.put("SKU-PEN", 1);
    }

    /**
     * 预留库存。
     * 检查库存是否充足，充足则扣减并返回 true，否则返回 false。
     *
     * @param sku      商品 SKU 编码
     * @param quantity 预留数量
     * @return true=预留成功，false=库存不足
     */
    @Override
    public boolean reserve(String sku, int quantity) {
        Integer available = stock.get(sku);
        // 库存不存在或不足，拒绝预留
        if (available == null || available < quantity) {
            return false;
        }
        // 扣减库存
        stock.put(sku, available - quantity);
        return true;
    }

    /**
     * 查询指定 SKU 的可用库存
     *
     * @param sku 商品 SKU 编码
     * @return 可用数量
     */
    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
