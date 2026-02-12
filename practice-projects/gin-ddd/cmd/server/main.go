package main

import (
	"context"
	"fmt"
	"log"
	"regexp"
	"time"

	"gin-ddd/pkg/utils"

	"gin-ddd/internal/application/service/order"
	"gin-ddd/internal/application/service/user"
	"gin-ddd/internal/domain/event"
	"gin-ddd/internal/domain/notification"
	"gin-ddd/internal/infrastructure/config"
	"gin-ddd/internal/infrastructure/mail"
	"gin-ddd/internal/infrastructure/mq"
	orderPersistence "gin-ddd/internal/infrastructure/persistence/order"
	userPersistence "gin-ddd/internal/infrastructure/persistence/user"
	orderHandler "gin-ddd/internal/interfaces/handler/order"
	userHandler "gin-ddd/internal/interfaces/handler/user"
	"gin-ddd/internal/interfaces/router"
	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/validator/v10"
)

func main() {
	// 初始化日志到控制台
	utils.InitLogger()

	// 注册自定义验证器
	registerValidators()
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

	// 初始化邮件服务
	var mailService notification.MailService
	if cfg.Mail.Enabled {
		utils.GetLogger().Info("初始化邮件服务...")
		mailService = mail.NewSMTPMailService(
			cfg.Mail.Host,
			cfg.Mail.Port,
			cfg.Mail.Username,
			cfg.Mail.Password,
			cfg.Mail.FromEmail,
			cfg.Mail.FromName,
		)
		utils.GetLogger().Info("邮件服务初始化成功")
	} else {
		utils.GetLogger().Info("邮件服务未启用")
	}

	// 初始化事件发布器（优先使用 RocketMQ，备选内存发布器）
	utils.GetLogger().Info("RocketMQ 启用状态: %v", cfg.RocketMQ.Enabled)
	var eventPublisher event.EventPublisher
	var rocketmqProducer *mq.RocketMQProducer

	if cfg.RocketMQ.Enabled {
		utils.GetLogger().Info("开始初始化 RocketMQ 生产者...")
		producer, err := mq.NewRocketMQProducer(
			cfg.RocketMQ.NameServer,
			cfg.RocketMQ.GroupName,
			cfg.RocketMQ.InstanceName,
			cfg.RocketMQ.RetryTimes,
		)
		if err != nil {
			utils.GetLogger().Error("初始化 RocketMQ 生产者失败: %v，使用内存事件发布器作为备选方案", err)
			eventPublisher = mq.NewMemoryEventPublisher()
		} else {
			rocketmqProducer = producer
			eventPublisher = producer
			defer producer.Close()
			utils.GetLogger().Info("RocketMQ 生产者初始化成功")

			// 启动消费者监听事件
			utils.GetLogger().Info("启动 RocketMQ 消费者...")
			go startEventConsumer(cfg, mailService)
		}
	} else {
		utils.GetLogger().Info("RocketMQ 未启用，使用内存事件发布器")
		eventPublisher = mq.NewMemoryEventPublisher()
	}

	// 确保 eventPublisher 不为 nil
	if eventPublisher == nil {
		panic("事件发布器初始化失败")
	}

	// 初始化应用服务
	utils.GetLogger().Info("初始化应用服务...")
	userService := user.NewUserService(userRepo)
	orderService := order.NewOrderServiceWithUserRepo(orderRepo, userRepo, eventPublisher)
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
func startEventConsumer(cfg *config.AppConfig, mailService notification.MailService) {
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
	// 创建一个包装函数，将 mailService 传递给事件处理器
	subscribeFunc := func(ctx context.Context, evt event.DomainEvent) error {
		return handleOrderEvent(ctx, evt, mailService)
	}
	err = consumer.Subscribe(orderTopic, subscribeFunc)
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
func handleOrderEvent(ctx context.Context, evt event.DomainEvent, mailService notification.MailService) error {
	utils.GetLogger().Info("处理订单事件: Type=%s, Data=%+v", evt.EventType(), evt.EventData())

	// 根据事件类型执行不同的业务逻辑
	switch evt.EventType() {
	case "order.created":
		utils.GetLogger().Info("接收到订单创建事件")
		if mailService != nil {
			// 从事件数据中提取订单信息
			if orderEvent, ok := evt.(*event.OrderEvent); ok {
				utils.GetLogger().Info("开始发送订单确认邮件")
				utils.GetLogger().Info("邮件收件人: email=%s, name=%s", orderEvent.UserEmail, orderEvent.UserName)
				utils.GetLogger().Info("订单信息: orderId=%d, orderNo=%s, amount=%.2f", orderEvent.OrderID, orderEvent.OrderNo, orderEvent.TotalAmount)

				orderData := map[string]interface{}{
					"order_id":     orderEvent.OrderID,
					"order_no":     orderEvent.OrderNo,
					"total_amount": orderEvent.TotalAmount,
					"status":       orderEvent.Status,
				}

				utils.GetLogger().Debug("调用MailService.SendOrderConfirmationMail()")
				if err := mailService.SendOrderConfirmationMail(ctx, orderEvent.UserEmail, orderEvent.UserName, orderData); err != nil {
					utils.GetLogger().Error("发送订单确认邮件失败: %v (不影响业务流程)", err)
				} else {
					utils.GetLogger().Info("订单确认邮件发送成功")
				}
			}
		} else {
			utils.GetLogger().Info("邮件服务未启用，跳过邮件发送")
		}
	case "order.paid":
		utils.GetLogger().Info("订单支付事件：可以触发发货流程、更新营销数据等")
	case "order.cancelled":
		utils.GetLogger().Info("订单取消事件：可以触发库存回滚、退款流程等")
	default:
		utils.GetLogger().Error("未知的订单事件类型: %s", evt.EventType())
	}

	return nil
}

// registerValidators 注册自定义验证器
func registerValidators() {
	if v, ok := binding.Validator.Engine().(*validator.Validate); ok {
		v.RegisterValidation("phone", func(fl validator.FieldLevel) bool {
			phone := fl.Field().String()
			// 手机号验证规则：11位数字，以1开头
			phoneRegex := regexp.MustCompile(`^1[3-9]\d{9}$`)
			return phoneRegex.MatchString(phone)
		})
	}
}
