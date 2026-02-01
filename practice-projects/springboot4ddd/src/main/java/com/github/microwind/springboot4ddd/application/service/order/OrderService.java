package com.github.microwind.springboot4ddd.application.service.order;

import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.dto.order.OrderMapper;
import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.producer.OrderEventProducer;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 订单应用服务
 * 协调领域对象完成业务用例，负责事务管理
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "orderTransactionManager", readOnly = true)  // 默认只读事务
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;  // 注入用户仓储，用于跨库查询
    private final OrderEventProducer orderEventProducer;  // 注入订单事件生产者

    /**
     * 创建订单
     *
     * @param request 创建订单请求
     * @return 订单DTO
     */
    @Transactional(transactionManager = "orderTransactionManager")  // 覆盖为读写事务
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("创建订单，userId={}, totalAmount={}", request.getUserId(), request.getTotalAmount());

        // 使用领域模型创建订单
        Order order = Order.create(request.getUserId(), request.getTotalAmount());

        // 持久化订单
        Order savedOrder = orderRepository.save(order);

        // 订单保存成功后记录创建事件
        savedOrder.recordCreatedEvent();

        // 发布领域事件到 RocketMQ
        publishDomainEvents(savedOrder);

        log.info("订单创建成功，orderNo={}", savedOrder.getOrderNo());
        return orderMapper.toDTO(savedOrder);
    }

    /**
     * 获取订单详情（不含用户信息）
     *
     * @param id 订单ID
     * @return 订单详情Response
     */
    public OrderResponse getOrderDetail(Long id) {
        log.info("查询订单详情，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        return orderMapper.toOrderResponse(order);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    // 继承类级别的只读事务
    public OrderDTO getOrder(Long id) {
        log.info("查询订单详情，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        return orderMapper.toDTO(order);
    }

    /**
     * 根据订单号获取订单
     *
     * @param orderNo 订单号
     * @return 订单DTO
     */
    // 继承类级别的只读事务
    public OrderDTO getOrderByNo(String orderNo) {
        log.info("根据订单号查询订单，orderNo={}", orderNo);

        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，orderNo=" + orderNo));

        return orderMapper.toDTO(order);
    }

    /**
     * 获取用户订单列表（包含用户信息）
     *
     * @param userId 用户ID
     * @return 订单列表Response
     */
    public List<OrderListResponse> getUserOrderList(Long userId) {
        log.info("查询用户订单列表，userId={}", userId);

        // 1. 查询订单列表（PostgreSQL）
        List<Order> orders = orderRepository.findByUserId(userId);

        // 2. 转换为带用户信息的Response（跨库查询MySQL）
        return convertToOrderListResponse(orders);
    }

    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @return 订单DTO列表
     */
    // 继承类级别的只读事务
    public List<OrderDTO> getUserOrders(Long userId) {
        log.info("查询用户订单列表，userId={}", userId);

        // 1. 查询订单列表（PostgreSQL）
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDTO> orderDTOList = orderMapper.toDTOList(orders);

        // 2. 跨库填充用户信息（MySQL）
        enrichUserInfoForOrders(orderDTOList);

        return orderDTOList;
    }

    /**
     * 获取所有订单（包含用户信息）
     *
     * @return 订单列表Response
     */
    public List<OrderListResponse> getAllOrderList() {
        log.info("查询所有订单");

        // 1. 查询订单列表（PostgreSQL）
        List<Order> orders = orderRepository.findAllOrders();

        // 2. 转换为带用户信息的Response（跨库查询MySQL）
        return convertToOrderListResponse(orders);
    }

    /**
     * 获取所有订单
     *
     * @return 订单DTO列表
     */
    // 继承类级别的只读事务
    public List<OrderDTO> getAllOrders() {
        log.info("查询所有订单");

        // 1. 查询订单列表（PostgreSQL）
        List<Order> orders = orderRepository.findAllOrders();
        List<OrderDTO> orderDTOList = orderMapper.toDTOList(orders);

        // 2. 跨库填充用户信息（MySQL）
        enrichUserInfoForOrders(orderDTOList);

        return orderDTOList;
    }

    /**
     * 将订单列表转换为带用户信息的Response列表（跨库查询）
     *
     * @param orders 订单列表
     * @return 订单列表Response
     */
    private List<OrderListResponse> convertToOrderListResponse(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderListResponse> result = new ArrayList<>();
        for (Order order : orders) {
            // 根据 userId 跨库查询用户信息（从MySQL）
            Optional<User> userOptional = userRepository.findById(order.getUserId());

            String userName = "未知用户";
            String userPhone = null;

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userName = user.getName();
                userPhone = user.getPhone();
                log.debug("已为订单 {} 填充用户信息: {}, {}",
                    order.getOrderNo(), userName, userPhone);
            } else {
                log.warn("订单 {} 关联的用户 {} 不存在", order.getOrderNo(), order.getUserId());
            }

            result.add(orderMapper.toListResponse(order, userName, userPhone));
        }

        return result;
    }

    /**
     * 为订单列表填充用户信息（跨库查询示例）
     * 从 MySQL 数据库查询用户姓名和电话，填充到订单 DTO 中
     *
     * @param orderDTOList 订单DTO列表
     */
    private void enrichUserInfoForOrders(List<OrderDTO> orderDTOList) {
        if (orderDTOList == null || orderDTOList.isEmpty()) {
            return;
        }

        for (OrderDTO orderDTO : orderDTOList) {
            // 根据 userId 跨库查询用户信息（从MySQL）
            Optional<User> userOptional = userRepository.findById(orderDTO.getUserId());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // 填充用户姓名和电话
                orderMapper.enrichUserInfo(orderDTO, user.getName(), user.getPhone());
                log.debug("已为订单 {} 填充用户信息: {}, {}",
                    orderDTO.getOrderNo(), user.getName(), user.getPhone());
            } else {
                // 如果用户不存在，设置默认值
                orderMapper.enrichUserInfo(orderDTO, "未知用户", null);
                log.warn("订单 {} 关联的用户 {} 不存在", orderDTO.getOrderNo(), orderDTO.getUserId());
            }
        }
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional(transactionManager = "orderTransactionManager")  // 覆盖为读写事务
    public OrderDTO cancelOrder(Long id) {
        log.info("取消订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的取消方法
        order.cancel();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        // 发布领域事件到 RocketMQ
        publishDomainEvents(updatedOrder);

        log.info("订单取消成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 支付订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional(transactionManager = "orderTransactionManager")  // 覆盖为读写事务
    public OrderDTO payOrder(Long id) {
        log.info("支付订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的支付方法
        order.pay();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        // 发布领域事件到 RocketMQ
        publishDomainEvents(updatedOrder);

        log.info("订单支付成功，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 订单DTO
     */
    @Transactional(transactionManager = "orderTransactionManager")  // 覆盖为读写事务
    public OrderDTO completeOrder(Long id) {
        log.info("完成订单，id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，id=" + id));

        // 调用领域模型的完成方法
        order.complete();

        // 持久化订单
        Order updatedOrder = orderRepository.save(order);

        // 发布领域事件到 RocketMQ
        publishDomainEvents(updatedOrder);

        log.info("订单完成，orderNo={}", updatedOrder.getOrderNo());
        return orderMapper.toDTO(updatedOrder);
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     */
    @Transactional(transactionManager = "orderTransactionManager")  // 覆盖为读写事务
    public void deleteOrder(Long id) {
        log.info("删除订单，id={}", id);

        if (!orderRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("订单不存在，id=" + id);
        }

        orderRepository.deleteById(id);
        log.info("订单删除成功，id={}", id);
    }

    /**
     * 发布领域事件到 RocketMQ
     *
     * @param order 订单聚合根
     */
    private void publishDomainEvents(Order order) {
        List<DomainEvent> events = order.getDomainEvents();
        if (events == null || events.isEmpty()) {
            return;
        }

        try {
            // 批量发布事件
            orderEventProducer.publishEvents(events);
            // 清空已发布的事件
            order.clearDomainEvents();
        } catch (Exception e) {
            log.error("发布订单领域事件失败，orderNo={}", order.getOrderNo(), e);
            // 根据业务需求决定是否重新抛出异常导致事务回滚
            throw e;
        }
    }
}
