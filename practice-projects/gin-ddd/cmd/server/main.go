package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"gin-ddd/internal/application/service/order"
	"gin-ddd/internal/application/service/user"
	"gin-ddd/internal/domain/event"
	"gin-ddd/internal/infrastructure/config"
	"gin-ddd/internal/infrastructure/mq"
	orderPersistence "gin-ddd/internal/infrastructure/persistence/order"
	userPersistence "gin-ddd/internal/infrastructure/persistence/user"
	orderHandler "gin-ddd/internal/interfaces/handler/order"
	userHandler "gin-ddd/internal/interfaces/handler/user"
	"gin-ddd/internal/interfaces/router"
)

func main() {
	// 加载配置
	cfg, err := config.LoadConfig("config/config.yaml")
	if err != nil {
		log.Fatalf("加载配置文件失败: %v", err)
	}

	// 初始化数据库
	userDBConfig := &config.DatabaseConfig{
		Driver:          cfg.Database.User.Driver,
		Host:            cfg.Database.User.Host,
		Port:            cfg.Database.User.Port,
		UserName:        cfg.Database.User.UserName,
		Password:        cfg.Database.User.Password,
		Database:        cfg.Database.User.Database,
		MaxOpenConns:    cfg.Database.User.MaxOpenConns,
		MaxIdleConns:    cfg.Database.User.MaxIdleConns,
		ConnMaxLifetime: time.Duration(cfg.Database.User.ConnMaxLifetime) * time.Second,
	}

	userDB, err := config.InitDatabase(userDBConfig)
	if err != nil {
		log.Fatalf("用户数据库初始化失败: %v", err)
	}
	defer userDB.Close()

	orderDBConfig := &config.DatabaseConfig{
		Driver:          cfg.Database.Order.Driver,
		Host:            cfg.Database.Order.Host,
		Port:            cfg.Database.Order.Port,
		UserName:        cfg.Database.Order.UserName,
		Password:        cfg.Database.Order.Password,
		Database:        cfg.Database.Order.Database,
		MaxOpenConns:    cfg.Database.Order.MaxOpenConns,
		MaxIdleConns:    cfg.Database.Order.MaxIdleConns,
		ConnMaxLifetime: time.Duration(cfg.Database.Order.ConnMaxLifetime) * time.Second,
	}

	orderDB, err := config.InitDatabase(orderDBConfig)
	if err != nil {
		log.Fatalf("订单数据库初始化失败: %v", err)
	}
	defer orderDB.Close()

	// 初始化仓储
	userRepo := userPersistence.NewUserRepository(userDB)
	orderRepo := orderPersistence.NewOrderRepository(orderDB)

	// 初始化 RocketMQ 生产者（可选）
	var eventPublisher event.EventPublisher
	if cfg.RocketMQ.Enabled {
		producer, err := mq.NewRocketMQProducer(
			cfg.RocketMQ.NameServer,
			cfg.RocketMQ.GroupName,
			cfg.RocketMQ.InstanceName,
			cfg.RocketMQ.RetryTimes,
		)
		if err != nil {
			log.Printf("初始化 RocketMQ 生产者失败: %v，将以非MQ模式运行", err)
		} else {
			eventPublisher = producer
			defer producer.Close()

			// 启动消费者监听事件
			go startEventConsumer(cfg)
		}
	}

	// 初始化应用服务
	userService := user.NewUserService(userRepo)
	orderService := order.NewOrderService(orderRepo, eventPublisher)

	// 初始化处理器
	userHandlerInstance := userHandler.NewUserHandler(userService)
	orderHandlerInstance := orderHandler.NewOrderHandler(orderService)

	// 配置路由
	r := router.NewRouter(userHandlerInstance, orderHandlerInstance)
	engine := r.Setup(cfg.Server.Mode)

	// 启动服务器
	addr := fmt.Sprintf("%s:%d", cfg.Server.Host, cfg.Server.Port)
	log.Printf("服务器启动成功，监听地址: %s", addr)
	if err := engine.Run(addr); err != nil {
		log.Fatalf("服务器启动失败: %v", err)
	}
}

// startEventConsumer 启动事件消费者
func startEventConsumer(cfg *config.AppConfig) {
	consumer, err := mq.NewRocketMQConsumer(
		cfg.RocketMQ.NameServer,
		cfg.RocketMQ.GroupName+"-consumer",
		cfg.RocketMQ.InstanceName+"-consumer",
	)
	if err != nil {
		log.Printf("创建 RocketMQ 消费者失败: %v", err)
		return
	}
	defer consumer.Close()

	// 订阅订单事件
	orderTopic := cfg.RocketMQ.Topics["order_event"]
	err = consumer.Subscribe(orderTopic, handleOrderEvent)
	if err != nil {
		log.Printf("订阅订单事件失败: %v", err)
		return
	}

	// 启动消费者
	if err := consumer.Start(); err != nil {
		log.Printf("启动消费者失败: %v", err)
		return
	}

	// 保持运行
	select {}
}

// handleOrderEvent 处理订单事件
func handleOrderEvent(ctx context.Context, event event.DomainEvent) error {
	log.Printf("处理订单事件: Type=%s, Data=%+v", event.EventType(), event.EventData())

	// 根据事件类型执行不同的业务逻辑
	switch event.EventType() {
	case "order.created":
		log.Println("订单创建事件：可以触发库存扣减、发送通知等")
	case "order.paid":
		log.Println("订单支付事件：可以触发发货流程、更新营销数据等")
	case "order.cancelled":
		log.Println("订单取消事件：可以触发库存回滚、退款流程等")
	default:
		log.Printf("未知的订单事件类型: %s", event.EventType())
	}

	return nil
}
