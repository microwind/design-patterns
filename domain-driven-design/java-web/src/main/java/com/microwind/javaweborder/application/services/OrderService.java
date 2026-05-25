package com.microwind.javaweborder.application.services;

import com.microwind.javaweborder.application.command.CreateOrderCommand;
import com.microwind.javaweborder.application.command.UpdateOrderCommand;
import com.microwind.javaweborder.application.dto.OrderDTO;
import com.microwind.javaweborder.domain.event.DomainEventPublisher;
import com.microwind.javaweborder.domain.event.OrderDeletedEvent;
import com.microwind.javaweborder.domain.exception.OrderNotFoundException;
import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Money;
import com.microwind.javaweborder.domain.order.Order;
import com.microwind.javaweborder.domain.order.OrderFactory;
import com.microwind.javaweborder.domain.order.OrderId;
import com.microwind.javaweborder.domain.repository.OrderRepository;
import com.microwind.javaweborder.domain.service.OrderPricingService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单应用服务（Application Service）。
 *
 * <p>DDD 战术构件：<b>应用服务</b>，DDD 的"用例编排者"。
 * <ul>
 *   <li>处理一次完整的用例（创建订单、取消订单等）</li>
 *   <li>协调仓储、工厂、领域服务、聚合根、事件发布器</li>
 *   <li>把握事务边界（真实工程由 {@code @Transactional} 等手段控制）</li>
 *   <li><b>不承载业务规则</b>——业务规则在领域层</li>
 * </ul>
 *
 * <h3>典型编排流程（createOrder）</h3>
 * <ol>
 *   <li>把命令对象解构为值对象（输入边界）</li>
 *   <li>调领域服务计算业务规则（折扣）</li>
 *   <li>用工厂创建聚合根</li>
 *   <li>通过仓储持久化</li>
 *   <li>发布聚合根累积的事件</li>
 *   <li>转 DTO 返回（输出边界）</li>
 * </ol>
 *
 * <h3>与重构前的差异</h3>
 * <ul>
 *   <li>原 generateOrderId 是应用服务静态方法 → 已下沉到领域层 {@link OrderFactory}</li>
 *   <li>原直接 {@code new Order(...)} 装配 → 改走 {@code OrderFactory.create()}</li>
 *   <li>原由 Service 自己拼字符串发消息 → 现由聚合根记录事件、Service 统一发布</li>
 *   <li>原裸的 {@code double + String} 参数 → 现包装为 {@link Money} / {@link CustomerName} 值对象</li>
 * </ul>
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final OrderPricingService pricingService;
    private final DomainEventPublisher eventPublisher;

    /**
     * 构造器注入：所有依赖通过构造器声明，便于测试与替换实现。
     *
     * <p>应用服务里"new 出依赖"是反模式（让单元测试无从下手）。
     * 由更外层（{@link com.microwind.javaweborder.Application} 组合根）组装依赖图。
     *
     * @param orderRepository 订单仓储
     * @param orderFactory    订单工厂
     * @param pricingService  定价领域服务
     * @param eventPublisher  领域事件发布器
     */
    public OrderService(OrderRepository orderRepository,
                        OrderFactory orderFactory,
                        OrderPricingService pricingService,
                        DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.pricingService = pricingService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 创建订单。
     *
     * @param command 创建命令
     * @return 创建后的订单 DTO
     */
    public OrderDTO createOrder(CreateOrderCommand command) {
        CustomerName customer = CustomerName.of(command.getCustomerName());
        Money originalAmount = Money.of(command.getAmount());
        Money finalAmount = pricingService.applyDiscount(customer, originalAmount);

        Order order = orderFactory.create(customer, finalAmount);
        orderRepository.save(order);
        eventPublisher.publishAll(order.pullDomainEvents());

        return OrderDTO.fromDomain(order);
    }

    /**
     * 取消订单。
     *
     * @param id 订单 ID
     * @throws OrderNotFoundException 订单不存在
     * @throws com.microwind.javaweborder.domain.exception.InvalidOrderStateException 当前状态不允许取消
     */
    public void cancelOrder(long id) {
        Order order = loadOrder(id);
        order.cancel();
        orderRepository.save(order);
        eventPublisher.publishAll(order.pullDomainEvents());
    }

    /**
     * 查询单个订单。
     *
     * @param id 订单 ID
     * @return 订单 DTO
     * @throws OrderNotFoundException 订单不存在
     */
    public OrderDTO getOrder(long id) {
        Order order = loadOrder(id);
        return OrderDTO.fromDomain(order);
    }

    /**
     * 更新订单。
     *
     * @param command 更新命令
     * @return 更新后的订单 DTO
     * @throws OrderNotFoundException 订单不存在
     * @throws com.microwind.javaweborder.domain.exception.InvalidOrderStateException 当前状态不允许修改
     */
    public OrderDTO updateOrder(UpdateOrderCommand command) {
        Order order = loadOrder(command.getOrderId());
        order.update(
                CustomerName.of(command.getCustomerName()),
                Money.of(command.getAmount())
        );
        orderRepository.save(order);
        eventPublisher.publishAll(order.pullDomainEvents());
        return OrderDTO.fromDomain(order);
    }

    /**
     * 删除订单。
     *
     * @param id 订单 ID
     * @throws OrderNotFoundException 订单不存在
     */
    public void deleteOrder(long id) {
        Order order = loadOrder(id);
        orderRepository.delete(order.getId());
        eventPublisher.publish(new OrderDeletedEvent(order.getId()));
    }

    /**
     * 列出全部订单（演示用，真实场景应分页）。
     *
     * @return 订单 DTO 列表
     */
    public List<OrderDTO> listOrder() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::fromDomain)
                .collect(Collectors.toList());
    }

    /**
     * 内部辅助：把 long 包装为 OrderId 并加载聚合根。
     * 把"加载并校验"集中一处，避免散落 orElseThrow。
     */
    private Order loadOrder(long id) {
        return orderRepository.findById(OrderId.of(id))
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
