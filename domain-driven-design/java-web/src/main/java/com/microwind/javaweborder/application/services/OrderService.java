// 应用层(Application) - 订单应用服务
//
// 应用服务（Application Service）是 DDD 的"用例编排者"：
// - 负责处理一次完整的用例（创建订单、取消订单等）
// - 协调仓储、工厂、领域服务、领域对象、事件发布器
// - 处理事务边界（在真实工程中由 @Transactional 等手段控制）
// - 不应承载业务规则（业务规则在领域层）
//
// 与"重构前"的差异：
// - 原先 generateOrderId 是应用服务的静态方法 → 现已下沉到领域层 OrderFactory
// - 原先直接 new Order(...) 装配状态 → 现走 OrderFactory.create()
// - 原先取消订单时由 Service 自己组装并发消息 → 现由 Order 记录事件、Service 统一发布
// - 原先用 double + String 直接传递 → 现包装为 Money、CustomerName 值对象
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

public class OrderService {

    private final OrderRepository orderRepository;        // 聚合根仓储
    private final OrderFactory orderFactory;              // 聚合工厂
    private final OrderPricingService pricingService;     // 领域服务（折扣策略）
    private final DomainEventPublisher eventPublisher;    // 事件发布器

    // 构造器注入：所有依赖通过构造器声明，便于测试与替换
    //
    // 注意：应用服务里"new 出依赖"是个反模式（会让单元测试无从下手）。
    // 由更外层（Application 入口）来组装依赖图。
    public OrderService(OrderRepository orderRepository,
                        OrderFactory orderFactory,
                        OrderPricingService pricingService,
                        DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.pricingService = pricingService;
        this.eventPublisher = eventPublisher;
    }

    // 创建订单：典型的"应用服务用例编排"流程
    // 1) 把命令对象解构为值对象（输入边界）
    // 2) 调领域服务计算业务规则（折扣）
    // 3) 用工厂创建聚合根
    // 4) 通过仓储持久化
    // 5) 发布聚合根累积的事件
    // 6) 转 DTO 返回（输出边界）
    public OrderDTO createOrder(CreateOrderCommand command) {
        CustomerName customer = CustomerName.of(command.getCustomerName());
        Money originalAmount = Money.of(command.getAmount());
        Money finalAmount = pricingService.applyDiscount(customer, originalAmount);

        Order order = orderFactory.create(customer, finalAmount);
        orderRepository.save(order);
        eventPublisher.publishAll(order.pullDomainEvents());

        return OrderDTO.fromDomain(order);
    }

    public void cancelOrder(long id) {
        Order order = loadOrder(id);
        order.cancel();
        orderRepository.save(order);
        eventPublisher.publishAll(order.pullDomainEvents());
    }

    public OrderDTO getOrder(long id) {
        Order order = loadOrder(id);
        return OrderDTO.fromDomain(order);
    }

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

    public void deleteOrder(long id) {
        Order order = loadOrder(id);
        orderRepository.delete(order.getId());
        eventPublisher.publish(new OrderDeletedEvent(order.getId()));
    }

    // 演示用，真实场景应分页
    public List<OrderDTO> listOrder() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::fromDomain)
                .collect(Collectors.toList());
    }

    // 把 long 包装为 OrderId 并加载聚合根；集中 orElseThrow 避免散落
    private Order loadOrder(long id) {
        return orderRepository.findById(OrderId.of(id))
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
