package src;

/**
 * InventoryClient - 库存服务契约接口
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：OrderService 依赖此接口而非具体实现，
 *     使得本地调用和远程调用可以通过替换实现类来切换。
 *   - 策略模式（Strategy Pattern）：不同的实现类（InventoryService / HttpInventoryClient）
 *     代表不同的调用策略，调用方通过接口统一访问。
 *
 * 【架构思想】
 *   在微服务架构中，服务间通过契约（接口/协议）解耦。调用方只依赖契约，
 *   不关心被调服务是本地实现还是远程服务。
 *
 * 【开源对比】
 *   - Spring Cloud OpenFeign：通过注解声明式地定义远程服务契约接口
 *   - gRPC：通过 .proto 文件定义服务契约，自动生成客户端接口
 *   本示例用 Java 原生接口模拟契约定义。
 */
public interface InventoryClient {

    /**
     * 预留库存
     *
     * @param sku      商品 SKU 编码
     * @param quantity 预留数量
     * @return true=预留成功，false=库存不足
     */
    boolean reserve(String sku, int quantity);
}
