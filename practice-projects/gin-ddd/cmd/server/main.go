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
	userClient "gin-ddd/internal/domain/client/user"
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
	"github.com/jmoiron/sqlx"
)

// 默认 MQ topic,配置中未提供时使用。
const (
	defaultOrderTopic = "order-event-topic"
	defaultUserTopic  = "user-event-topic"
)

func main() {
	utils.InitLogger()
	registerValidators()

	utils.GetLogger().Info("========================================")
	utils.GetLogger().Info("应用程序启动")
	utils.GetLogger().Info("========================================")

	cfg, err := config.LoadConfig("config/config.yaml")
	if err != nil {
		log.Fatalf("加载配置文件失败: %v", err)
	}
	utils.GetLogger().Info("配置加载成功,模式: %s", cfg.Server.Mode)

	userDB := mustInitDB(cfg.Database.User, "用户")
	defer userDB.Close()

	orderDB := mustInitDB(cfg.Database.Order, "订单")
	defer orderDB.Close()

	// 仓储
	userRepo := userPersistence.NewUserRepository(userDB)
	orderRepo := orderPersistence.NewOrderRepository(orderDB)

	// 领域服务 / 防腐层端口的基础设施实现
	uniquenessChecker := userPersistence.NewUniquenessChecker(userRepo)
	userInfoQueryClient := userPersistence.NewUserInfoQueryClient(userRepo)

	// 邮件
	var mailService notification.MailService
	if cfg.Mail.Enabled {
		mailService = mail.NewSMTPMailService(
			cfg.Mail.Host, cfg.Mail.Port,
			cfg.Mail.Username, cfg.Mail.Password,
			cfg.Mail.FromEmail, cfg.Mail.FromName,
		)
		utils.GetLogger().Info("邮件服务初始化成功")
	} else {
		utils.GetLogger().Info("邮件服务未启用")
	}

	// 事件发布器
	eventPublisher := initEventPublisher(cfg, mailService, userInfoQueryClient)

	// 应用服务
	orderTopic := topicOr(cfg.RocketMQ.Topics, "order_event", defaultOrderTopic)
	userTopic := topicOr(cfg.RocketMQ.Topics, "user_event", defaultUserTopic)

	userService := user.NewUserService(userRepo, uniquenessChecker, eventPublisher, userTopic)
	orderService := order.NewOrderService(orderRepo, eventPublisher, orderTopic)

	// HTTP 处理器
	r := router.NewRouter(
		userHandler.NewUserHandler(userService),
		orderHandler.NewOrderHandler(orderService),
	)
	engine := r.Setup(cfg.Server.Mode)

	addr := fmt.Sprintf("%s:%d", cfg.Server.Host, cfg.Server.Port)
	utils.GetLogger().Info("服务器启动,监听: %s", addr)
	if err := engine.Run(addr); err != nil {
		log.Fatalf("服务器启动失败: %v", err)
	}
}

// mustInitDB 初始化数据库,失败则致命退出。
func mustInitDB(info config.DatabaseInfo, label string) *sqlx.DB {
	dbCfg := &config.DatabaseConfig{
		Driver:          info.Driver,
		Host:            info.Host,
		Port:            info.Port,
		UserName:        info.UserName,
		Password:        info.Password,
		Database:        info.Database,
		MaxOpenConns:    info.MaxOpenConns,
		MaxIdleConns:    info.MaxIdleConns,
		ConnMaxLifetime: time.Duration(info.ConnMaxLifetime) * time.Second,
	}
	db, err := config.InitDatabase(dbCfg)
	if err != nil {
		log.Fatalf("%s数据库初始化失败: %v", label, err)
	}
	utils.GetLogger().Info("%s数据库连接成功: %s://%s:%d/%s", label, info.Driver, info.Host, info.Port, info.Database)
	return db
}

// initEventPublisher 优先 RocketMQ,失败降级内存发布器;同时启动消费者监听订单事件。
func initEventPublisher(cfg *config.AppConfig, mailService notification.MailService, userInfoQueryClient userClient.UserInfoQueryClient) event.EventPublisher {
	if !cfg.RocketMQ.Enabled {
		utils.GetLogger().Info("RocketMQ 未启用,使用内存事件发布器")
		return mq.NewMemoryEventPublisher()
	}

	producer, err := mq.NewRocketMQProducer(
		cfg.RocketMQ.NameServer, cfg.RocketMQ.GroupName,
		cfg.RocketMQ.InstanceName, cfg.RocketMQ.RetryTimes,
	)
	if err != nil {
		utils.GetLogger().Error("RocketMQ 生产者初始化失败: %v,降级为内存发布器", err)
		return mq.NewMemoryEventPublisher()
	}

	utils.GetLogger().Info("RocketMQ 生产者初始化成功")
	go startEventConsumer(cfg, mailService, userInfoQueryClient)
	return producer
}

// topicOr 从 topics 配置中取 key,缺失时返回 fallback。
func topicOr(topics map[string]string, key, fallback string) string {
	if v, ok := topics[key]; ok && v != "" {
		return v
	}
	return fallback
}

// startEventConsumer 启动 RocketMQ 消费者,订阅订单事件。
//
// 跨上下文的用户信息(发邮件需要的用户名/邮箱)通过 userInfoQueryClient
// 在消费端按需查询,不再耦合事件本身字段——事件只承载订单聚合根状态。
func startEventConsumer(cfg *config.AppConfig, mailService notification.MailService, userInfoQueryClient userClient.UserInfoQueryClient) {
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

	orderTopic := topicOr(cfg.RocketMQ.Topics, "order_event", defaultOrderTopic)
	utils.GetLogger().Info("订阅订单事件,Topic: %s", orderTopic)

	subscribe := func(ctx context.Context, evt event.DomainEvent) error {
		return handleOrderEvent(ctx, evt, mailService, userInfoQueryClient)
	}
	if err := consumer.Subscribe(orderTopic, subscribe); err != nil {
		utils.GetLogger().Error("订阅订单事件失败: %v", err)
		return
	}
	if err := consumer.Start(); err != nil {
		utils.GetLogger().Error("启动消费者失败: %v", err)
		return
	}

	utils.GetLogger().Info("消费者已启动,等待事件...")
	select {}
}

// handleOrderEvent 订单事件处理。
//
// 邮件需要的用户名/邮箱通过 UserInfoQueryClient 在此处查询,
// 把"跨上下文数据补齐"封装在消费端,而非污染事件本身。
func handleOrderEvent(ctx context.Context, evt event.DomainEvent, mailService notification.MailService, userInfoQueryClient userClient.UserInfoQueryClient) error {
	utils.GetLogger().Info("处理订单事件: Type=%s", evt.EventType())

	orderEvent, ok := evt.(*event.OrderEvent)
	if !ok {
		utils.GetLogger().Warn("非订单事件类型,跳过: %s", evt.EventType())
		return nil
	}

	switch evt.EventType() {
	case event.OrderCreatedEvent:
		if mailService == nil {
			utils.GetLogger().Info("邮件服务未启用,跳过订单确认邮件")
			return nil
		}

		brief, err := userInfoQueryClient.FindBriefByID(ctx, orderEvent.UserID)
		if err != nil {
			utils.GetLogger().Error("查询用户简介失败: %v, userId=%d", err, orderEvent.UserID)
			return nil
		}
		if brief == nil {
			utils.GetLogger().Warn("订单关联用户不存在,跳过邮件: userId=%d", orderEvent.UserID)
			return nil
		}

		orderData := map[string]interface{}{
			"order_id":     orderEvent.OrderID,
			"order_no":     orderEvent.OrderNo,
			"total_amount": orderEvent.TotalAmount,
			"status":       orderEvent.Status,
		}
		if err := mailService.SendOrderConfirmationMail(ctx, brief.Email, brief.Name, orderData); err != nil {
			utils.GetLogger().Error("发送订单确认邮件失败: %v (不影响业务流程)", err)
		} else {
			utils.GetLogger().Info("订单确认邮件发送成功: orderNo=%s, email=%s", orderEvent.OrderNo, brief.Email)
		}
	case event.OrderPaidEvent:
		utils.GetLogger().Info("订单支付事件: 可触发发货流程/营销数据更新")
	case event.OrderCancelledEvent:
		utils.GetLogger().Info("订单取消事件: 可触发库存回滚/退款流程")
	default:
		utils.GetLogger().Debug("订单事件未配置处理逻辑: %s", evt.EventType())
	}
	return nil
}

// registerValidators 注册自定义验证器。
func registerValidators() {
	if v, ok := binding.Validator.Engine().(*validator.Validate); ok {
		v.RegisterValidation("phone", func(fl validator.FieldLevel) bool {
			phoneRegex := regexp.MustCompile(`^1[3-9]\d{9}$`)
			return phoneRegex.MatchString(fl.Field().String())
		})
	}
}
