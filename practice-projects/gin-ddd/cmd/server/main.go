package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"gin-ddd/pkg/utils"

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
	// 初始化日志到控制台
	utils.InitLogger()
	utils.GetLogger().Info("========================================")
	utils.GetLogger().Info("应用程序启动")
	utils.GetLogger().Info("========================================")

	// 加载配置
	utils.GetLogger().Info("开始加载配置文件...")
	cfg, err := config.LoadConfig("config/config.yaml")
	if err != nil {
		utils.GetLogger().Error("加载配置文件失败: %v", err)
		log.Fatalf("加载配置文件失败: %v", err)
	}
	utils.GetLogger().Info("配置文件加载成功，服务器模式: %s", cfg.Server.Mode)

	// 初始化数据库
	utils.GetLogger().Info("开始初始化用户数据库连接...")
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
	utils.GetLogger().Info("用户数据库连接成功: %s://%s:%d/%s", userDBConfig.Driver, userDBConfig.Host, userDBConfig.Port, userDBConfig.Database)

	utils.GetLogger().Info("开始初始化订单数据库连接...")
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
	utils.GetLogger().Info("订单数据库连接成功: %s://%s:%d/%s", orderDBConfig.Driver, orderDBConfig.Host, orderDBConfig.Port, orderDBConfig.Database)

	// 初始化仓储
	utils.GetLogger().Info("初始化数据仓储...")
	userRepo := userPersistence.NewUserRepository(userDB)
	orderRepo := orderPersistence.NewOrderRepository(orderDB)
	utils.GetLogger().Info("数据仓储初始化完成")

	// 初始化 RocketMQ 生产者（可选）
	utils.GetLogger().Info("RocketMQ 启用状态: %v", cfg.RocketMQ.Enabled)
	var eventPublisher event.EventPublisher
	if cfg.RocketMQ.Enabled {
		utils.GetLogger().Info("开始初始化 RocketMQ 生产者...")
		producer, err := mq.NewRocketMQProducer(
			cfg.RocketMQ.NameServer,
			cfg.RocketMQ.GroupName,
			cfg.RocketMQ.InstanceName,
			cfg.RocketMQ.RetryTimes,
		)
		if err != nil {
			utils.GetLogger().Error("初始化 RocketMQ 生产者失败: %v，将以非MQ模式运行", err)
		} else {
			eventPublisher = producer
			defer producer.Close()
			utils.GetLogger().Info("RocketMQ 生产者初始化成功")

			// 启动消费者监听事件
			utils.GetLogger().Info("启动 RocketMQ 消费者...")
			go startEventConsumer(cfg)
		}
	} else {
		utils.GetLogger().Info("RocketMQ 未启用，使用内存事件发布模式")
	}

	// 初始化应用服务
	utils.GetLogger().Info("初始化应用服务...")
	userService := user.NewUserService(userRepo)
	orderService := order.NewOrderService(orderRepo, eventPublisher)
	utils.GetLogger().Info("应用服务初始化完成")

	// 初始化处理器
	utils.GetLogger().Info("初始化请求处理器...")
	userHandlerInstance := userHandler.NewUserHandler(userService)
	orderHandlerInstance := orderHandler.NewOrderHandler(orderService)
	utils.GetLogger().Info("请求处理器初始化完成")

	// 配置路由
	utils.GetLogger().Info("配置路由...")
	r := router.NewRouter(userHandlerInstance, orderHandlerInstance)
	engine := r.Setup(cfg.Server.Mode)

	// 启动服务器
	addr := fmt.Sprintf("%s:%d", cfg.Server.Host, cfg.Server.Port)
	utils.GetLogger().Info("服务器启动成功，监听地址: %s", addr)
	if err := engine.Run(addr); err != nil {
		utils.GetLogger().Error("服务器启动失败: %v", err)
		log.Fatalf("服务器启动失败: %v", err)
	}
}

// startEventConsumer 启动事件消费者
func startEventConsumer(cfg *config.AppConfig) {
	utils.GetLogger().Info("创建 RocketMQ 消费者...")
	consumer, err := mq.NewRocketMQConsumer(
		cfg.RocketMQ.NameServer,
		cfg.RocketMQ.GroupName+"-consumer",
		cfg.RocketMQ.InstanceName+"-consumer",
	)
	if err != nil {
		utils.GetLogger().Error("创建 RocketMQ 消费者失败: %v", err)
		return
	}
	defer consumer.Close()
	utils.GetLogger().Info("RocketMQ 消费者创建成功")

	// 订阅订单事件
	orderTopic := cfg.RocketMQ.Topics["order_event"]
	utils.GetLogger().Info("订阅订单事件，Topic: %s", orderTopic)
	err = consumer.Subscribe(orderTopic, handleOrderEvent)
	if err != nil {
		utils.GetLogger().Error("订阅订单事件失败: %v", err)
		return
	}
	utils.GetLogger().Info("订单事件订阅成功")

	// 启动消费者
	utils.GetLogger().Info("启动消费者监听...")
	if err := consumer.Start(); err != nil {
		utils.GetLogger().Error("启动消费者失败: %v", err)
		return
	}

	// 保持运行
	utils.GetLogger().Info("消费者已启动，等待事件...")
	select {}
}

// handleOrderEvent 处理订单事件
func handleOrderEvent(ctx context.Context, event event.DomainEvent) error {
	utils.GetLogger().Info("处理订单事件: Type=%s, Data=%+v", event.EventType(), event.EventData())

	// 根据事件类型执行不同的业务逻辑
	switch event.EventType() {
	case "order.created":
		utils.GetLogger().Info("订单创建事件：可以触发库存扣减、发送通知等")
	case "order.paid":
		utils.GetLogger().Info("订单支付事件：可以触发发货流程、更新营销数据等")
	case "order.cancelled":
		utils.GetLogger().Info("订单取消事件：可以触发库存回滚、退款流程等")
	default:
		utils.GetLogger().Error("未知的订单事件类型: %s", event.EventType())
	}

	return nil
}
