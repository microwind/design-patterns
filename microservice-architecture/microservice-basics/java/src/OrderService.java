package src;

/**
 * OrderService - 订单服务（核心业务服务）
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：依赖 InventoryClient 接口而非具体实现，
 *     通过构造函数注入，实现了控制反转（IoC）。
 *   - 外观模式（Facade Pattern）：对外提供 createOrder 统一入口，
 *     隐藏了库存检查和订单创建的内部流程。
 *   - 策略模式（Strategy Pattern）：通过注入不同的 InventoryClient 实现，
 *     可以在不修改本类的情况下切换调用策略（本地/远程）。
 *
 * 【架构思想】
 *   OrderService 是微服务拆分的核心示例：它只关心业务逻辑（创建订单），
 *   不关心库存服务的具体位置和实现。这种解耦使得服务可以独立部署和扩展。
 *
 * 【开源对比】
 *   - Spring Boot 中的 @Service + @Autowired 实现依赖注入
 *   - Spring Cloud OpenFeign 自动为远程服务生成代理实现
 *   本示例用构造函数注入模拟 IoC 容器的行为。
 */
public class OrderService {

    /** 库存服务客户端（通过接口注入，支持本地/远程切换） */
    private final InventoryClient inventoryClient;

    /**
     * 构造订单服务
     *
     * @param inventoryClient 库存服务客户端（可以是本地实现或 HTTP 远程客户端）
     */
    public OrderService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    /**
     * 创建订单。
     * 先调用库存服务预留库存，根据结果决定订单状态。
     *
     * @param orderId  订单ID
     * @param sku      商品 SKU 编码
     * @param quantity 订购数量
     * @return 创建的订单（status 为 "CREATED" 或 "REJECTED"）
     */
    public Order createOrder(String orderId, String sku, int quantity) {
        // 通过契约接口调用库存服务（不关心是本地还是远程）
        boolean reserved = inventoryClient.reserve(sku, quantity);
        if (reserved) {
            return new Order(orderId, sku, quantity, "CREATED");
        }
        return new Order(orderId, sku, quantity, "REJECTED");
    }
}
