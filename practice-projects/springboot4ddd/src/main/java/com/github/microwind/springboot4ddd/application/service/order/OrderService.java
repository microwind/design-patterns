package com.github.microwind.springboot4ddd.application.service.order;

import com.github.microwind.springboot4ddd.application.command.order.CreateOrderCommand;
import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.dto.order.OrderListView;
import com.github.microwind.springboot4ddd.application.dto.order.OrderMapper;
import com.github.microwind.springboot4ddd.application.port.CachePolicy;
import com.github.microwind.springboot4ddd.application.port.CacheService;
import com.github.microwind.springboot4ddd.domain.client.user.UserBriefInfo;
import com.github.microwind.springboot4ddd.domain.client.user.UserInfoQueryClient;
import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import com.github.microwind.springboot4ddd.domain.event.DomainEventPublisher;
import com.github.microwind.springboot4ddd.domain.exception.EntityNotFoundException;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单应用服务
 *
 * <p>只负责用例编排与事务管理。业务规则下沉到 {@link Order} 聚合根；
 * 跨上下文的用户信息通过 {@link UserInfoQueryClient}（防腐层接口）一次批量拉取；
 * 领域事件通过 {@link DomainEventPublisher}（领域端口）发布，
 * 不直接依赖具体 MQ 实现。
 *
 * <p>输入参数为 {@code application.command} 包下的 Command 对象，
 * 输出统一为 {@code application.dto} 下的 DTO/View，不引用 interfaces 层。
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "orderTransactionManager", readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserInfoQueryClient userInfoQueryClient;
    private final DomainEventPublisher domainEventPublisher;
    private final CacheService cacheService;

    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO createOrder(CreateOrderCommand command) {
        log.info("创建订单，userId={}, totalAmount={}", command.getUserId(), command.getTotalAmount());

        Order order = Order.create(command.getUserId(), command.getTotalAmount());
        Order savedOrder = orderRepository.save(order);
        publishDomainEvents(savedOrder);

        log.info("订单创建成功，orderNo={}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    public OrderDTO getOrderDetail(Long id) {
        return cacheService.getOrSet(
                CachePolicy.ORDER_KEY_PREFIX + "id:" + id,
                CachePolicy.ORDER_TTL,
                () -> {
                    Order order = orderRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("订单", "id", id));
                    return orderMapper.toDTO(order);
                }
        );
    }

    public OrderDTO getOrderByNo(String orderNo) {
        return cacheService.getOrSet(
                CachePolicy.ORDER_KEY_PREFIX + "no:" + orderNo,
                CachePolicy.ORDER_TTL,
                () -> {
                    Order order = orderRepository.findByOrderNo(orderNo)
                            .orElseThrow(() -> new EntityNotFoundException("订单", "orderNo", orderNo));
                    return orderMapper.toDTO(order);
                }
        );
    }

    public List<OrderListView> getUserOrderList(Long userId) {
        return convertToOrderListView(orderRepository.findByUserId(userId));
    }

    public PageResult<OrderListView> getUserOrderList(Long userId, PageRequest pageRequest) {
        PageResult<Order> orderPage = orderRepository.findByUserId(userId, pageRequest);
        List<OrderListView> views = convertToOrderListView(orderPage.getContent());
        return new PageResult<>(views, orderPage.getTotalElements(),
                pageRequest.getPageNumber(), pageRequest.getPageSize());
    }

    public List<OrderListView> getAllOrderList() {
        return convertToOrderListView(orderRepository.findAllOrders());
    }

    public PageResult<OrderListView> getAllOrderList(PageRequest pageRequest) {
        PageResult<Order> orderPage = orderRepository.findAllOrders(pageRequest);
        List<OrderListView> views = convertToOrderListView(orderPage.getContent());
        return new PageResult<>(views, orderPage.getTotalElements(),
                pageRequest.getPageNumber(), pageRequest.getPageSize());
    }

    /**
     * 批量为订单列表组装用户简介，一次查询消除 N+1。
     */
    private List<OrderListView> convertToOrderListView(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, UserBriefInfo> userMap = userInfoQueryClient.findBriefs(userIds);

        List<OrderListView> result = new ArrayList<>(orders.size());
        for (Order order : orders) {
            UserBriefInfo info = userMap.get(order.getUserId());
            String userName = info != null ? info.getName() : "未知用户";
            String userPhone = info != null ? info.getPhone() : null;
            if (info == null) {
                log.warn("订单 {} 关联的用户 {} 不存在", order.getOrderNo(), order.getUserId());
            }
            result.add(orderMapper.toListView(order, userName, userPhone));
        }
        return result;
    }

    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO cancelOrder(Long id) {
        log.info("取消订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单", "id", id));
        order.cancel();
        Order updatedOrder = orderRepository.save(order);
        publishDomainEvents(updatedOrder);
        evictOrderCaches(updatedOrder);

        log.info("订单取消成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO payOrder(Long id) {
        log.info("支付订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单", "id", id));
        order.pay();
        Order updatedOrder = orderRepository.save(order);
        publishDomainEvents(updatedOrder);
        evictOrderCaches(updatedOrder);

        log.info("订单支付成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional(transactionManager = "orderTransactionManager")
    public OrderDTO completeOrder(Long id) {
        log.info("完成订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单", "id", id));
        order.complete();
        Order updatedOrder = orderRepository.save(order);
        publishDomainEvents(updatedOrder);
        evictOrderCaches(updatedOrder);

        log.info("订单完成，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional(transactionManager = "orderTransactionManager")
    public void deleteOrder(Long id) {
        log.info("删除订单，id={}", id);

        if (!orderRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("订单", "id", id);
        }
        orderRepository.deleteById(id);
        cacheService.delete(CachePolicy.ORDER_KEY_PREFIX + "id:" + id);
        log.info("订单删除成功，id={}", id);
    }

    private void evictOrderCaches(Order order) {
        cacheService.delete(CachePolicy.ORDER_KEY_PREFIX + "id:" + order.getId());
        cacheService.delete(CachePolicy.ORDER_KEY_PREFIX + "no:" + order.getOrderNo());
    }

    private void publishDomainEvents(Order order) {
        List<DomainEvent> events = order.getDomainEvents();
        if (events == null || events.isEmpty()) {
            return;
        }
        try {
            domainEventPublisher.publishEvents(events);
            order.clearDomainEvents();
        } catch (Exception e) {
            log.error("发布订单领域事件失败，orderNo={}", order.getOrderNo(), e);
            throw e;
        }
    }
}
