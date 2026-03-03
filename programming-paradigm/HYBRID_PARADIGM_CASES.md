# 混合编程范式实战案例集

> 本文档提供6个完整的混合编程范式实战案例，展示如何在真实项目中结合多种范式

---

## 案例1: 电商平台订单系统

### 系统架构

```
Frontend (React)
    ↓ EDP事件 + RP响应流
Backend API (Spring Boot)
    ├─ OOP服务层
    ├─ AOP事务与日志
    └─ FP数据处理
    ↓
数据库 + 消息队列
    ↓ EDP事件驱动
通知服务、库存服务、支付服务
```

### 完整代码实现

#### 后端：Spring Boot应用

```java
// ========== 1. OOP: 领域模型 ==========
@Entity
@Data
public class Order {
    @Id
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime createdAt;

    // 状态流转 (State Pattern + OOP)
    public void pay() {
        if (status == OrderStatus.PENDING) {
            this.status = OrderStatus.PAID;
        }
    }

    public void ship() {
        if (status == OrderStatus.PAID) {
            this.status = OrderStatus.SHIPPED;
        }
    }
}

@Entity
@Data
public class OrderItem {
    @Id
    private Long id;
    @ManyToOne
    private Order order;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}

// ========== 2. OOP: 资源库层 ==========
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    List<Order> findByStatusAndCreatedAtAfter(OrderStatus status, LocalDateTime date);
}

// ========== 3. AOP: 事务与日志切面 ==========
@Aspect
@Component
public class OrderAspect {
    private static final Logger logger = LoggerFactory.getLogger(OrderAspect.class);

    // 前置通知：记录方法调用
    @Before("execution(* com.shop.service.OrderService.*(..))")
    public void logBefore(JoinPoint jp) {
        logger.info("Call: {} with args: {}",
            jp.getSignature().getName(),
            Arrays.toString(jp.getArgs()));
    }

    // 环绕通知：性能监控
    @Around("execution(* com.shop.service.OrderService.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("Method {} took {} ms",
                pjp.getSignature().getName(), duration);
        }
    }

    // 异常通知：异常处理
    @AfterThrowing(pointcut = "execution(* com.shop.service.OrderService.*(..))",
                   throwing = "ex")
    public void handleException(JoinPoint jp, Exception ex) {
        logger.error("Exception in {} : {}",
            jp.getSignature().getName(), ex.getMessage());
    }
}

// ========== 4. OOP: 业务服务层 ==========
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // 事务自动织入 (AOP)
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. 验证与计算
        List<OrderItem> items = request.getItems().stream()
            .map(this::validateItem)
            .collect(Collectors.toList());

        // 2. 创建订单
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(calculateTotal(items));

        // 3. 保存订单
        Order saved = orderRepository.save(order);

        // 4. 发布事件 (EDP模式)
        eventPublisher.publishEvent(new OrderCreatedEvent(saved));

        return saved;
    }

    // ========== FP: 纯函数处理数据 ==========
    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItem validateItem(CreateOrderRequest.Item item) {
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        OrderItem oi = new OrderItem();
        oi.setProductId(item.getProductId());
        oi.setQuantity(item.getQuantity());
        oi.setPrice(item.getPrice());
        return oi;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(OrderDTO::from)
            .sorted(Comparator.comparing(OrderDTO::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
}

// ========== 5. EDP: 事件监听 ==========
@Component
public class OrderEventListener {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        logger.info("Order created: {}", event.getOrder().getId());
        // 发送通知
        sendNotification(event.getOrder());
        // 更新库存
        updateInventory(event.getOrder());
    }

    @EventListener
    public void onOrderPaid(OrderPaidEvent event) {
        logger.info("Order paid: {}", event.getOrder().getId());
        // 发起发货流程
        initiateShipping(event.getOrder());
    }

    private void sendNotification(Order order) {
        // 发送邮件/短信
    }

    private void updateInventory(Order order) {
        // 扣减库存
    }

    private void initiateShipping(Order order) {
        // 生成发货单
    }
}

// ========== 6. RP: 响应式查询 ==========
@Component
public class OrderReactiveService {
    @Autowired
    private OrderRepository orderRepository;

    public Flux<OrderDTO> getOrdersStream(Long userId) {
        return Mono.fromCallable(() -> orderRepository.findByUserId(userId))
            .flatMapMany(Flux::fromIterable)
            .map(OrderDTO::from)
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError(e -> logger.error("Error in order stream", e));
    }

    public Mono<OrderStatistics> getOrderStatistics(Long userId) {
        return Mono.fromCallable(() -> {
            List<Order> orders = orderRepository.findByUserId(userId);
            return calculateStatistics(orders);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private OrderStatistics calculateStatistics(List<Order> orders) {
        return OrderStatistics.builder()
            .totalOrders(orders.size())
            .totalAmount(
                orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            )
            .averageAmount(
                orders.isEmpty() ? BigDecimal.ZERO :
                orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP)
            )
            .build();
    }
}

// ========== 7. OOP: REST Controller ==========
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderReactiveService reactiveService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.ok(OrderDTO.from(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @GetMapping("/stream")
    public Flux<OrderDTO> getOrdersStream(@RequestParam Long userId) {
        return reactiveService.getOrdersStream(userId);
    }
}
```

#### 前端：React应用

```javascript
// ========== 1. EDP: 事件系统 ==========
class EventBus {
    constructor() {
        this.listeners = {};
    }

    on(event, handler) {
        if (!this.listeners[event]) {
            this.listeners[event] = [];
        }
        this.listeners[event].push(handler);
    }

    emit(event, data) {
        if (this.listeners[event]) {
            this.listeners[event].forEach(h => h(data));
        }
    }
}

const eventBus = new EventBus();

// ========== 2. RP: 响应式状态管理 ==========
import { Subject, BehaviorSubject, Observable } from 'rxjs';
import { map, filter, scan } from 'rxjs/operators';

class OrderStore {
    constructor() {
        // 订单列表流
        this.orders$ = new BehaviorSubject([]);

        // 订单操作事件流
        this.orderActions$ = new Subject();

        // 聚合计算统计
        this.statistics$ = this.orders$.pipe(
            map(orders => ({
                total: orders.length,
                totalAmount: orders.reduce((sum, o) => sum + o.totalAmount, 0),
                avgAmount: orders.length ?
                    orders.reduce((sum, o) => sum + o.totalAmount, 0) / orders.length : 0
            }))
        );
    }

    // FP: 纯函数状态更新
    addOrder(order) {
        const current = this.orders$.value;
        this.orders$.next([...current, order]);
        this.orderActions$.next({ type: 'ORDER_ADDED', payload: order });
    }

    getOrders$() {
        return this.orders$.asObservable();
    }

    getStatistics$() {
        return this.statistics$.asObservable();
    }
}

const orderStore = new OrderStore();

// ========== 3. OOP + RP: React组件 ==========
function OrderList() {
    const [orders, setOrders] = useState([]);
    const [statistics, setStatistics] = useState(null);

    useEffect(() => {
        // 订阅订单流 (RP)
        const subscription1 = orderStore.getOrders$()
            .subscribe(orders => {
                setOrders(orders);
                // EDP: 发送事件
                eventBus.emit('orders-updated', orders);
            });

        // 订阅统计流 (RP)
        const subscription2 = orderStore.getStatistics$()
            .subscribe(stats => setStatistics(stats));

        // 监听外部事件 (EDP)
        eventBus.on('order-status-changed', (data) => {
            console.log('Order status changed:', data);
        });

        return () => {
            subscription1.unsubscribe();
            subscription2.unsubscribe();
        };
    }, []);

    return (
        <div>
            <h2>订单列表</h2>
            <OrderStatistics data={statistics} />
            <ul>
                {orders.map(order => (
                    <OrderCard key={order.id} order={order} />
                ))}
            </ul>
        </div>
    );
}

// ========== 4. FP: 数据处理函数 ==========
const transformOrderForDisplay = (order) => ({
    ...order,
    formattedAmount: `¥${order.totalAmount.toFixed(2)}`,
    statusLabel: {
        'PENDING': '待支付',
        'PAID': '已支付',
        'SHIPPED': '已发货',
        'DELIVERED': '已收货'
    }[order.status]
});

const filterActiveOrders = (orders) =>
    orders.filter(o => ['PENDING', 'PAID', 'SHIPPED'].includes(o.status));

const sortByCreatedAt = (orders) =>
    [...orders].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

const processOrders = (orders) =>
    sortByCreatedAt(
        filterActiveOrders(orders)
    ).map(transformOrderForDisplay);

// ========== 5. OOP: 组件类 ==========
class OrderCard extends React.Component {
    constructor(props) {
        super(props);
        this.state = { expanded: false };
    }

    toggleExpand = () => {
        this.setState(prev => ({ expanded: !prev.expanded }));
        eventBus.emit('card-toggled', { orderId: this.props.order.id });
    }

    handlePay = async () => {
        try {
            const response = await fetch(`/api/orders/${this.props.order.id}/pay`, {
                method: 'POST'
            });
            const paid = await response.json();
            orderStore.addOrder(paid);
            eventBus.emit('order-paid', { order: paid });
        } catch (error) {
            console.error('Payment failed:', error);
            eventBus.emit('order-error', { error });
        }
    }

    render() {
        const { order } = this.props;
        return (
            <li className="order-card">
                <header onClick={this.toggleExpand}>
                    <span>{order.id}</span>
                    <span>{order.formattedAmount}</span>
                    <span className={`status ${order.status}`}>
                        {order.statusLabel}
                    </span>
                </header>
                {this.state.expanded && (
                    <div className="order-details">
                        <OrderItems items={order.items} />
                        {order.status === 'PENDING' && (
                            <button onClick={this.handlePay}>支付</button>
                        )}
                    </div>
                )}
            </li>
        );
    }
}
```

### 范式总结

| 层级 | 范式 | 作用 |
|-----|-----|------|
| **后端架构** | OOP | 领域模型、服务、仓储 |
| **事务管理** | AOP | 自动织入事务控制 |
| **数据处理** | FP | 聚合、转换、计算 |
| **系统通信** | EDP | 订单事件、库存更新 |
| **前端状态** | RP | 流式状态管理 |
| **前端界面** | OOP | 组件结构 |

---

## 案例2: 实时监控仪表板

### 系统架构

```
数据源 (传感器/日志)
    ↓ (EDP事件)
消息队列 (Kafka)
    ↓ (RP流处理)
Flink实时处理
    ├─ FP数据聚合
    └─ 指标计算
    ↓ (RP推送)
WebSocket
    ↓ (RP订阅)
React组件 (实时更新)
```

### 关键代码

```java
// ========== Flink流处理 (FP + RP) ==========
public class MonitoringPipeline {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
            StreamExecutionEnvironment.getExecutionEnvironment();

        // 1. 数据源 (EDP: 事件流)
        DataStream<MetricEvent> events = env
            .addSource(new KafkaSource<>(kafkaConfig))
            .map(json -> parseMetricEvent(json));

        // 2. 流处理 (FP: 纯函数转换)
        DataStream<MetricAggregation> aggregated = events
            .filter(e -> e.getValue() > 0)  // FP: filter
            .map(e -> new Metric(e.getName(), e.getValue()))  // FP: map
            .keyBy(Metric::getName)
            .window(TumblingEventTimeWindows.of(Time.minutes(1)))
            .aggregate(
                new AggregateFunction<Metric, MetricAccumulator, MetricAggregation>() {
                    @Override
                    public MetricAccumulator createAccumulator() {
                        return new MetricAccumulator();
                    }

                    @Override
                    public MetricAccumulator add(Metric value, MetricAccumulator acc) {
                        acc.add(value.getValue());
                        return acc;
                    }

                    @Override
                    public MetricAggregation getResult(MetricAccumulator acc) {
                        return acc.toAggregation();
                    }

                    @Override
                    public MetricAccumulator merge(MetricAccumulator a, MetricAccumulator b) {
                        a.merge(b);
                        return a;
                    }
                }
            );

        // 3. 多路处理 (FP: 分支处理)
        // 告警处理
        aggregated
            .filter(a -> a.getAvg() > THRESHOLD)  // FP: filter
            .map(a -> new Alert(a, AlertLevel.HIGH))  // FP: map
            .addSink(new AlertSink());

        // 4. WebSocket推送 (RP: 推送实时数据)
        aggregated
            .addSink(new WebSocketSink<>());

        env.execute("Monitoring Pipeline");
    }
}

// ========== Spring WebSocket (OOP + RP) ==========
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MetricsWebSocketHandler(), "/metrics");
    }
}

@Component
public class MetricsWebSocketHandler extends TextWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    // 接收来自Flink的聚合结果
    public void broadcastMetrics(MetricAggregation agg) {
        String json = objectMapper.writeValueAsString(agg);
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    logger.error("Error sending message", e);
                }
            }
        });
    }
}

// ========== React组件 (RP + OOP) ==========
function MetricsDashboard() {
    const [metrics, setMetrics] = useState({});

    useEffect(() => {
        // RP: WebSocket连接与订阅
        const ws = new WebSocket('ws://localhost:8080/metrics');

        ws.onmessage = (event) => {
            const agg = JSON.parse(event.data);
            // FP: 数据转换与合并
            setMetrics(prev => ({
                ...prev,
                [agg.name]: {
                    ...agg,
                    trend: calculateTrend(prev[agg.name], agg)
                }
            }));
        };

        return () => ws.close();
    }, []);

    // FP: 数据处理函数
    const calculateTrend = (prev, current) => {
        if (!prev) return 'stable';
        return current.avg > prev.avg ? 'up' : 'down';
    };

    return (
        <div className="dashboard">
            {Object.entries(metrics).map(([name, data]) => (
                <MetricCard key={name} name={name} data={data} />
            ))}
        </div>
    );
}
```

---

## 案例3: 游戏引擎循环

### 完整示例

```cpp
// ========== PP: 主游戏循环 (高性能顺序执行) ==========
class GameEngine {
private:
    std::vector<GameObject*> gameObjects;
    EventManager eventManager;
    RenderSystem renderSystem;

public:
    void run() {
        while (isRunning) {
            // 1. Input Processing (PP: 顺序执行)
            handleInput();

            // 2. Physics Update (PP)
            updatePhysics(deltaTime);

            // 3. Game Logic (OOP: 对象交互)
            updateGameObjects(deltaTime);

            // 4. Event Processing (EDP: 事件分发)
            processEvents();

            // 5. Render (PP: 优化渲染)
            render();

            // 帧速率控制
            limitFrameRate();
        }
    }

private:
    void handleInput() {
        // PP: 直接命令式处理
        if (keyboard.isPressed(KEY_W)) {
            player->moveForward();
        }
        if (mouse.leftButtonPressed()) {
            player->shoot();
        }
    }

    void updatePhysics(float dt) {
        // PP: 高性能物理更新 (不能有任何开销)
        for (auto& obj : gameObjects) {
            obj->position += obj->velocity * dt;
            obj->velocity += obj->acceleration * dt;
            applyCollisions(obj);
        }
    }

    void updateGameObjects(float dt) {
        // OOP: 多态调用
        for (auto& obj : gameObjects) {
            obj->update(dt);  // 虚函数调用
        }
    }

    void processEvents() {
        // EDP: 事件分发 (解耦系统)
        Event event;
        while (eventManager.pollEvent(event)) {
            switch (event.type) {
            case EventType::COLLISION:
                onCollision(event);
                break;
            case EventType::PLAYER_DIED:
                onPlayerDied(event);
                break;
            // ...
            }
        }
    }

    void render() {
        // PP: 批量渲染优化
        clearScreen();

        // 按深度排序 (FP: 排序函数)
        std::sort(gameObjects.begin(), gameObjects.end(),
            [](const GameObject* a, const GameObject* b) {
                return a->depth < b->depth;
            });

        // 批量绘制
        for (const auto& obj : gameObjects) {
            renderSystem.render(obj);
        }

        swapBuffers();
    }
};

// ========== OOP: 游戏对象基类 ==========
class GameObject {
protected:
    Vector3 position;
    Vector3 velocity;
    Vector3 acceleration;
    float depth;
    bool active;

public:
    virtual ~GameObject() = default;
    virtual void update(float dt) = 0;
    virtual void onCollide(GameObject* other) = 0;
    virtual void render() const = 0;
};

// ========== OOP: 具体游戏对象 ==========
class Player : public GameObject {
private:
    float health;
    int ammo;

public:
    void update(float dt) override {
        // 玩家特定逻辑
        updateAnimation(dt);
        checkBounds();
    }

    void shoot() {
        if (ammo > 0) {
            // 创建子弹 (EDP: 发送事件)
            Bullet* bullet = new Bullet(position, direction);
            eventManager.fire(BulletCreatedEvent(bullet));
            ammo--;
        }
    }

    void onCollide(GameObject* other) override {
        // EDP: 处理碰撞事件
        eventManager.fire(CollisionEvent(this, other));
    }

    void moveForward() { velocity.z += acceleration.z; }
    void moveBackward() { velocity.z -= acceleration.z; }
};

class Enemy : public GameObject {
private:
    float attackCooldown;
    AIBehavior* behavior;

public:
    void update(float dt) override {
        // AI行为 (OOP策略模式)
        behavior->update(this, dt);
        updateAnimation(dt);
    }

    void onCollide(GameObject* other) override {
        // Enemy碰撞处理
    }
};

// ========== EDP: 事件系统 ==========
class EventManager {
private:
    std::queue<Event> eventQueue;
    std::unordered_map<EventType, std::vector<EventHandler>> handlers;

public:
    void subscribe(EventType type, EventHandler handler) {
        handlers[type].push_back(handler);
    }

    void fire(const Event& event) {
        eventQueue.push(event);
    }

    void processEvents() {
        while (!eventQueue.empty()) {
            Event event = eventQueue.front();
            eventQueue.pop();

            for (auto& handler : handlers[event.type]) {
                handler(event);
            }
        }
    }
};
```

---

## 案例4: 微服务架构（Kafka消息驱动）

### 系统架构

```
┌─────────────┐
│  API网关    │
└──────┬──────┘
       │
  ┌────┴────┬──────────┬─────────┐
  ↓         ↓          ↓         ↓
用户服务   订单服务    支付服务   库存服务
(OOP+AOP) (OOP+EDP) (FP+RP)  (OOP+FP)
  │         │          │         │
  └─────────┼──────────┼─────────┘
            ↓
      Kafka消息队列
      (EDP事件总线)
            ↓
  ┌─────────┴─────────┐
  ↓                   ↓
通知服务            分析服务
(RP响应式)         (FP数据处理)
```

### 完整代码实现

#### 用户服务（OOP + AOP）

```java
// ========== OOP: 用户领域模型 ==========
@Entity
@Data
public class User {
    @Id
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    @Version
    private Long version;  // 乐观锁
}

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    // AOP自动织入事务
    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        // EDP: 发布用户创建事件
        kafkaTemplate.send("user-events",
            new UserCreatedEvent(saved.getId(), saved.getName()));

        return saved;
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userService.createUser(req));
    }
}
```

#### 订单服务（OOP + EDP）

```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, PaymentRequest> paymentKafka;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);

        // EDP: 发送订单创建事件
        kafkaTemplate.send("order-events",
            new OrderCreatedEvent(saved.getId(), saved.getUserId()));

        return saved;
    }

    // EDP: 监听支付完成事件
    @KafkaListener(topics = "payment-events")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
            .orElseThrow();

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // 发送确认事件
        kafkaTemplate.send("order-events",
            new OrderPaidEvent(order.getId()));
    }
}
```

#### 支付服务（FP + RP）

```java
@Service
public class PaymentService {
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    // FP: 纯函数验证支付
    private boolean validatePayment(PaymentRequest request) {
        return request.getAmount().compareTo(BigDecimal.ZERO) > 0
            && request.getCard() != null
            && validateCard(request.getCard());
    }

    private boolean validateCard(CardInfo card) {
        // FP: 纯函数式验证
        return card.getCardNumber() != null
            && card.getCvv() != null
            && !isCardExpired(card);
    }

    // RP: 异步响应式支付处理
    @KafkaListener(topics = "payment-requests")
    public Mono<Void> processPayment(PaymentRequest request) {
        return Mono.fromCallable(() -> validatePayment(request))
            .flatMap(valid -> {
                if (valid) {
                    return callPaymentGateway(request)
                        .flatMap(result -> publishEvent(
                            new PaymentCompletedEvent(request.getOrderId())));
                } else {
                    return publishEvent(
                        new PaymentFailedEvent(request.getOrderId(), "Invalid payment"));
                }
            });
    }

    // RP: 响应式HTTP调用
    private Mono<PaymentResult> callPaymentGateway(PaymentRequest request) {
        return webClient.post()
            .uri("https://payment-gateway.com/process")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(PaymentResult.class)
            .timeout(Duration.ofSeconds(10))
            .onErrorReturn(new PaymentResult(false, "Gateway error"));
    }

    private Mono<Void> publishEvent(PaymentEvent event) {
        return Mono.fromCallable(() -> {
            kafkaTemplate.send("payment-events", event);
            return null;
        });
    }
}
```

#### 库存服务（OOP + FP）

```java
@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    // EDP: 监听订单创建事件
    @KafkaListener(topics = "order-events")
    @Transactional
    public void onOrderCreated(OrderCreatedEvent event) {
        // OOP: 库存实体操作
        List<Inventory> items = getInventoryForOrder(event.getOrderId());

        // FP: 函数式处理库存
        boolean allAvailable = items.stream()
            .allMatch(this::hasStock);

        if (allAvailable) {
            items.forEach(inv -> inv.decreaseStock(1));
            inventoryRepository.saveAll(items);

            kafkaTemplate.send("inventory-events",
                new StockReservedEvent(event.getOrderId()));
        } else {
            kafkaTemplate.send("inventory-events",
                new StockUnavailableEvent(event.getOrderId()));
        }
    }

    // FP: 纯函数检查库存
    private boolean hasStock(Inventory inventory) {
        return inventory.getQuantity() > 0;
    }
}
```

### 微服务范式应用总结

| 组件 | 主范式 | 理由 |
|-----|-------|------|
| API网关 | OOP | 服务路由和请求处理 |
| 用户服务 | OOP + AOP | 业务逻辑+横切关注 |
| 订单服务 | OOP + EDP | 订单管理+事件驱动 |
| 支付服务 | FP + RP | 函数纯粹性+异步响应 |
| 库存服务 | OOP + FP | 数据管理+函数处理 |
| 消息队列 | EDP | 事件驱动+服务解耦 |

---

## 案例5: AI/ML数据处理系统

### 系统架构

```
数据源
  │
  ↓
数据采集层 (EDP事件监听)
  │
  ↓
数据清洗层 (FP纯函数)
  │
  ↓
特征工程 (FP函数组合)
  │
  ↓
模型训练 (OOP机器学习框架)
  │
  ↓
模型推理 (FP + OOP)
  │
  ↓
结果输出 (RP异步响应)
```

### 完整代码实现

#### 数据处理管道（FP + 纯函数）

```python
# ========== FP: 纯函数数据清洗 ==========
from functools import reduce
from typing import List, Callable

class DataProcessor:
    # FP: 纯函数，无副作用
    @staticmethod
    def remove_nulls(records: List[dict]) -> List[dict]:
        """移除包含空值的记录"""
        return [r for r in records if all(v is not None for v in r.values())]

    @staticmethod
    def normalize_values(records: List[dict]) -> List[dict]:
        """标准化数值"""
        return [
            {k: (float(v) - 100) / 50 if isinstance(v, (int, float)) else v
             for k, v in record.items()}
            for record in records
        ]

    @staticmethod
    def remove_outliers(records: List[dict], threshold: float = 3.0) -> List[dict]:
        """移除异常值"""
        return [r for r in records
                if all(abs(float(v)) < threshold for v in r.values()
                      if isinstance(v, (int, float)))]

    # FP: 函数组合
    @staticmethod
    def compose(*functions: Callable) -> Callable:
        """函数组合：f(g(h(x)))"""
        return reduce(lambda f, g: lambda x: f(g(x)),
                     functions, lambda x: x)

    @staticmethod
    def pipe(*functions: Callable) -> Callable:
        """管道：h(g(f(x)))"""
        return reduce(lambda f, g: lambda x: g(f(x)),
                     functions, lambda x: x)

# FP: 创建数据处理管道
clean_pipeline = DataProcessor.pipe(
    DataProcessor.remove_nulls,
    DataProcessor.normalize_values,
    DataProcessor.remove_outliers
)

# 使用管道
records = fetch_raw_data()
cleaned_records = clean_pipeline(records)
```

#### 特征工程（FP + 高阶函数）

```python
# ========== FP: 特征转换 ==========
import pandas as pd
from typing import Tuple

class FeatureEngineer:
    # FP: 高阶函数，返回转换函数
    @staticmethod
    def create_polynomial_features(degree: int) -> Callable:
        """创建多项式特征生成器"""
        def transform(X):
            features = [X]
            for d in range(2, degree + 1):
                features.append(X ** d)
            return pd.concat(features, axis=1)
        return transform

    # FP: 高阶函数，创建归一化器
    @staticmethod
    def create_normalizer(train_data) -> Callable:
        """创建基于训练数据的归一化函数"""
        mean = train_data.mean()
        std = train_data.std()

        def normalize(data):
            return (data - mean) / std
        return normalize

    # FP: 函数式特征选择
    @staticmethod
    def select_features_by_importance(features: pd.DataFrame,
                                     importance_scores: List[float],
                                     threshold: float = 0.01) -> Callable:
        """返回保留重要特征的函数"""
        important_cols = [col for col, score in
                         zip(features.columns, importance_scores)
                         if score > threshold]

        return lambda df: df[important_cols]

# FP: 特征处理管道
def create_feature_pipeline(train_data: pd.DataFrame):
    """FP范式：组合多个纯函数"""
    polynomial = FeatureEngineer.create_polynomial_features(2)
    normalizer = FeatureEngineer.create_normalizer(train_data)
    selector = FeatureEngineer.select_features_by_importance(
        train_data, [0.8, 0.6, 0.3, 0.2, 0.1])

    return DataProcessor.pipe(polynomial, normalizer, selector)

features_pipeline = create_feature_pipeline(train_data)
processed_features = features_pipeline(raw_features)
```

#### 模型训练（OOP + FP）

```python
# ========== OOP: 模型基类 ==========
from abc import ABC, abstractmethod
import tensorflow as tf

class BaseModel(ABC):
    """OOP: 模型抽象基类"""
    def __init__(self, name: str):
        self.name = name
        self.model = None
        self.history = None

    @abstractmethod
    def build(self):
        """构建模型"""
        pass

    @abstractmethod
    def train(self, X_train, y_train, epochs=10):
        """训练模型"""
        pass

    def predict(self, X):
        """预测"""
        return self.model.predict(X)

    def save(self, path: str):
        """保存模型"""
        self.model.save(path)

# ========== OOP: 神经网络模型 ==========
class NeuralNetworkModel(BaseModel):
    """OOP: 神经网络具体实现"""
    def __init__(self, input_dim: int, name: str = "nn"):
        super().__init__(name)
        self.input_dim = input_dim
        self.build()

    def build(self):
        """构建神经网络"""
        self.model = tf.keras.Sequential([
            tf.keras.layers.Dense(64, activation='relu',
                                 input_dim=self.input_dim),
            tf.keras.layers.Dropout(0.2),
            tf.keras.layers.Dense(32, activation='relu'),
            tf.keras.layers.Dropout(0.2),
            tf.keras.layers.Dense(1, activation='sigmoid')
        ])
        self.model.compile(
            optimizer='adam',
            loss='binary_crossentropy',
            metrics=['accuracy']
        )

    def train(self, X_train, y_train, epochs=10):
        """OOP: 训练模型"""
        self.history = self.model.fit(
            X_train, y_train,
            epochs=epochs,
            batch_size=32,
            validation_split=0.2,
            verbose=1
        )

# OOP: 使用模型
model = NeuralNetworkModel(input_dim=20)
model.train(X_train, y_train, epochs=50)
predictions = model.predict(X_test)
```

#### 推理与结果处理（FP + RP）

```python
# ========== FP: 纯函数推理 ==========
import asyncio
import aiohttp

class MLInference:
    @staticmethod
    def batch_predict(model, batch_data: List[dict]) -> List[float]:
        """FP: 纯函数批量预测"""
        return model.predict([item['features'] for item in batch_data])

    @staticmethod
    def post_process_predictions(predictions: List[float]) -> List[dict]:
        """FP: 纯函数结果后处理"""
        return [
            {
                'score': float(pred),
                'label': 'positive' if pred > 0.5 else 'negative',
                'confidence': max(pred, 1-pred)
            }
            for pred in predictions
        ]

# ========== RP: 异步响应式推理 ==========
class ReactiveMLService:
    def __init__(self, model):
        self.model = model

    async def async_predict_batch(self, batch_data: List[dict]) -> List[dict]:
        """RP: 异步预测"""
        # 异步数据库查询
        features = await self._fetch_features_async(batch_data)

        # 异步模型推理
        predictions = await asyncio.to_thread(
            self.model.predict,
            features
        )

        # FP: 结果后处理
        return MLInference.post_process_predictions(predictions)

    async def stream_predictions(self, data_stream) -> AsyncGenerator:
        """RP: 流式预测"""
        async for batch in data_stream:
            results = await self.async_predict_batch(batch)
            for result in results:
                yield result

    async def _fetch_features_async(self, batch_data):
        """异步特征获取"""
        async with aiohttp.ClientSession() as session:
            tasks = [self._get_features(session, item)
                    for item in batch_data]
            return await asyncio.gather(*tasks)

    async def _get_features(self, session, item):
        """异步获取单个样本特征"""
        async with session.get(f"/api/features/{item['id']}") as resp:
            return await resp.json()
```

### AI/ML范式应用总结

| 层级 | 主范式 | 理由 |
|-----|-------|------|
| 数据采集 | EDP | 事件驱动数据流 |
| 数据清洗 | FP | 纯函数无副作用 |
| 特征工程 | FP | 高阶函数组合 |
| 模型训练 | OOP | 模型对象管理 |
| 推理引擎 | FP + OOP | 纯函数+对象 |
| 异步处理 | RP | 异步响应流 |

---

## 案例6: 移动应用（React Native + Redux）

### 应用架构

```
┌─────────────────────┐
│   User Interface    │
│   (React Components)│
└──────────┬──────────┘
           │ EDP事件 + RP订阅
           ↓
┌─────────────────────┐
│   Redux Store       │
│   (OOP状态管理)    │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│   Reducers          │
│   (FP纯函数)       │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│   Actions           │
│   (EDP事件)        │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│   API Services      │
│   (RP异步)         │
└─────────────────────┘
```

### 完整代码实现

#### 应用状态管理（OOP + FP）

```javascript
// ========== OOP: Redux Store配置 ==========
import { createStore, applyMiddleware, combineReducers } from 'redux';
import thunk from 'redux-thunk';

// 应用状态形状定义
class AppState {
    constructor() {
        this.user = {
            id: null,
            name: '',
            email: '',
            loading: false,
            error: null
        };

        this.products = {
            items: [],
            loading: false,
            error: null,
            filter: { category: '', price: [0, 1000] }
        };

        this.cart = {
            items: [],
            total: 0,
            loading: false
        };
    }
}

// ========== FP: 纯函数Reducers ==========
// FP: 用户reducer (纯函数)
const userReducer = (state = new AppState().user, action) => {
    switch (action.type) {
        case 'USER_LOADING':
            return { ...state, loading: true };

        case 'USER_LOADED':
            return {
                ...state,
                id: action.payload.id,
                name: action.payload.name,
                email: action.payload.email,
                loading: false,
                error: null
            };

        case 'USER_ERROR':
            return { ...state, loading: false, error: action.payload };

        case 'USER_LOGOUT':
            return new AppState().user;

        default:
            return state;
    }
};

// FP: 商品reducer (纯函数)
const productReducer = (state = new AppState().products, action) => {
    // FP: 使用高阶函数处理过滤
    const applyFilters = (items, filter) =>
        items.filter(item =>
            item.category === filter.category &&
            item.price >= filter.price[0] &&
            item.price <= filter.price[1]
        );

    switch (action.type) {
        case 'PRODUCTS_LOADED':
            return {
                ...state,
                items: action.payload,
                loading: false,
                error: null
            };

        case 'FILTER_CHANGED':
            return {
                ...state,
                filter: action.payload
            };

        case 'PRODUCTS_ERROR':
            return { ...state, loading: false, error: action.payload };

        default:
            return state;
    }
};

// FP: 购物车reducer (纯函数)
const cartReducer = (state = new AppState().cart, action) => {
    // FP: 纯函数添加到购物车
    const addToCart = (items, product) => {
        const existing = items.find(item => item.id === product.id);
        if (existing) {
            return items.map(item =>
                item.id === product.id
                    ? { ...item, quantity: item.quantity + 1 }
                    : item
            );
        }
        return [...items, { ...product, quantity: 1 }];
    };

    // FP: 计算购物车总金额（reduce）
    const calculateTotal = (items) =>
        items.reduce((sum, item) => sum + item.price * item.quantity, 0);

    switch (action.type) {
        case 'ADD_TO_CART':
            const newItems = addToCart(state.items, action.payload);
            return {
                ...state,
                items: newItems,
                total: calculateTotal(newItems)
            };

        case 'REMOVE_FROM_CART':
            const filtered = state.items.filter(item => item.id !== action.payload);
            return {
                ...state,
                items: filtered,
                total: calculateTotal(filtered)
            };

        case 'CLEAR_CART':
            return { ...state, items: [], total: 0 };

        default:
            return state;
    }
};

// ========== Redux Store ==========
const rootReducer = combineReducers({
    user: userReducer,
    products: productReducer,
    cart: cartReducer
});

const store = createStore(
    rootReducer,
    applyMiddleware(thunk)
);
```

#### 异步操作（EDP + RP）

```javascript
// ========== EDP: Action Creators (事件发布) ==========
const userActions = {
    // EDP: 异步登录操作
    login: (email, password) => async (dispatch) => {
        dispatch({ type: 'USER_LOADING' });

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();
                dispatch({
                    type: 'USER_LOADED',
                    payload: data
                });
            } else {
                dispatch({
                    type: 'USER_ERROR',
                    payload: 'Login failed'
                });
            }
        } catch (error) {
            dispatch({
                type: 'USER_ERROR',
                payload: error.message
            });
        }
    },

    logout: () => ({ type: 'USER_LOGOUT' })
};

const productActions = {
    // EDP: 异步加载商品
    loadProducts: () => async (dispatch) => {
        try {
            const response = await fetch('/api/products');
            const data = await response.json();
            dispatch({
                type: 'PRODUCTS_LOADED',
                payload: data
            });
        } catch (error) {
            dispatch({
                type: 'PRODUCTS_ERROR',
                payload: error.message
            });
        }
    },

    setFilter: (filter) => ({
        type: 'FILTER_CHANGED',
        payload: filter
    })
};

const cartActions = {
    addToCart: (product) => ({
        type: 'ADD_TO_CART',
        payload: product
    }),

    removeFromCart: (productId) => ({
        type: 'REMOVE_FROM_CART',
        payload: productId
    }),

    clearCart: () => ({ type: 'CLEAR_CART' }),

    // EDP: 异步结账
    checkout: () => async (dispatch, getState) => {
        const { cart, user } = getState();

        try {
            const response = await fetch('/api/orders', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: user.id,
                    items: cart.items,
                    total: cart.total
                })
            });

            if (response.ok) {
                dispatch({ type: 'CLEAR_CART' });
            }
        } catch (error) {
            console.error('Checkout failed:', error);
        }
    }
};
```

#### React组件（RP响应式 + OOP）

```javascript
// ========== RP: 响应式组件 ==========
import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';

// RP: 产品列表组件（自动响应状态变化）
const ProductList = () => {
    const dispatch = useDispatch();
    const products = useSelector(state => state.products);
    const cart = useSelector(state => state.cart);
    const [filteredProducts, setFilteredProducts] = useState([]);

    // RP: 响应产品加载
    useEffect(() => {
        dispatch(productActions.loadProducts());
    }, [dispatch]);

    // RP: 响应过滤条件变化（自动过滤）
    useEffect(() => {
        const filtered = products.items.filter(item =>
            item.price >= products.filter.price[0] &&
            item.price <= products.filter.price[1]
        );
        setFilteredProducts(filtered);
    }, [products.items, products.filter]);

    if (products.loading) return <div>Loading...</div>;
    if (products.error) return <div>Error: {products.error}</div>;

    return (
        <div>
            <h2>Products</h2>
            {filteredProducts.map(product => (
                <ProductCard
                    key={product.id}
                    product={product}
                    onAddToCart={() => dispatch(cartActions.addToCart(product))}
                    isInCart={cart.items.some(item => item.id === product.id)}
                />
            ))}
        </div>
    );
};

// OOP: 产品卡片组件
class ProductCard extends React.Component {
    render() {
        const { product, onAddToCart, isInCart } = this.props;

        return (
            <div style={{ border: '1px solid #ccc', padding: '10px' }}>
                <h3>{product.name}</h3>
                <p>Price: ${product.price}</p>
                <p>{product.description}</p>
                <button
                    onClick={onAddToCart}
                    disabled={isInCart}
                >
                    {isInCart ? 'In Cart' : 'Add to Cart'}
                </button>
            </div>
        );
    }
}

// RP: 购物车组件（响应购物车变化）
const ShoppingCart = () => {
    const dispatch = useDispatch();
    const cart = useSelector(state => state.cart);

    // RP: 响应购物车项目变化
    useEffect(() => {
        // 购物车变化时的副作用
        console.log('Cart updated:', cart.total);
    }, [cart.items, cart.total]);

    return (
        <div>
            <h2>Shopping Cart</h2>
            {cart.items.length === 0 ? (
                <p>Empty cart</p>
            ) : (
                <>
                    <ul>
                        {cart.items.map(item => (
                            <li key={item.id}>
                                {item.name} x {item.quantity} = ${item.price * item.quantity}
                                <button onClick={() =>
                                    dispatch(cartActions.removeFromCart(item.id))
                                }>
                                    Remove
                                </button>
                            </li>
                        ))}
                    </ul>
                    <h3>Total: ${cart.total}</h3>
                    <button onClick={() => dispatch(cartActions.checkout())}>
                        Checkout
                    </button>
                </>
            )}
        </div>
    );
};

// RP: 用户信息组件
const UserInfo = () => {
    const user = useSelector(state => state.user);
    const dispatch = useDispatch();

    return (
        <div>
            {user.id ? (
                <>
                    <p>Welcome, {user.name}</p>
                    <button onClick={() => dispatch(userActions.logout())}>
                        Logout
                    </button>
                </>
            ) : (
                <p>Please login</p>
            )}
        </div>
    );
};
```

### 移动应用范式应用总结

| 层级 | 主范式 | 理由 |
|-----|-------|------|
| 状态管理 | OOP + FP | Redux对象管理+纯函数reducer |
| 组件 | RP + OOP | 响应式更新+类组件 |
| 数据流 | EDP + FP | 事件action+纯函数处理 |
| 异步操作 | RP | 异步thunk中间件 |
| UI逻辑 | FP | 高阶函数、Map、Filter |

---

## 范式对标总结

| 案例 | 主范式 | 辅助范式 | 理由 |
|-----|-------|--------|------|
| **电商平台** | OOP | AOP, FP, EDP, RP | 业务复杂，需要多角度处理 |
| **监控仪表** | FP + RP | OOP | 数据流处理和实时反应 |
| **游戏引擎** | PP | OOP, EDP | 性能优先，效率关键 |
| **微服务架构** | OOP + EDP | AOP, FP, RP | 服务治理+事件驱动+跨服务通信 |
| **AI/ML系统** | FP + OOP | EDP, RP | 纯函数数据处理+对象模型管理 |
| **移动应用** | OOP + FP | EDP, RP | Redux状态管理+函数式reducer |

---

## 核心要点

### ✅ 范式融合最佳实践

1. **清晰分工** - 每个范式负责特定职责
2. **避免混杂** - 不混合不兼容的思想
3. **性能优先** - 关键路径用PP
4. **可维护性** - 业务逻辑用OOP
5. **数据处理** - 使用FP
6. **异步响应** - 使用EDP/RP

### ❌ 常见错误

1. 将FP纯函数与OOP可变状态混合
2. 在性能关键路径使用过重的范式
3. 过度设计，一个简单问题用多个范式
4. 忽视框架生态支持

---

## 参考资源

- [编程范式综合对比](./COMPREHENSIVE_COMPARISON.md)
- [范式选择指南](./PARADIGM_SELECTION_GUIDE.md)
- [各范式详细实现](./programming-paradigm/)

*最后更新: 2026年3月2日*
