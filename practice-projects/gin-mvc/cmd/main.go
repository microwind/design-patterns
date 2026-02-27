package main

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"gin-mvc/internal/config"
	homectl "gin-mvc/internal/controllers/home"
	orderctl "gin-mvc/internal/controllers/order"
	userctl "gin-mvc/internal/controllers/user"
	"gin-mvc/internal/middleware"
	"gin-mvc/internal/models/event"
	dbconn "gin-mvc/internal/repository/db"
	mailrepo "gin-mvc/internal/repository/mail"
	mqrepo "gin-mvc/internal/repository/mq"
	orderrepo "gin-mvc/internal/repository/order"
	userrepo "gin-mvc/internal/repository/user"
	eventsvc "gin-mvc/internal/services/event"
	"gin-mvc/internal/services/notification"
	ordersvc "gin-mvc/internal/services/order"
	usersvc "gin-mvc/internal/services/user"
	"gin-mvc/pkg/logger"

	"github.com/gin-gonic/gin"
)

func main() {
	cfgPath := os.Getenv("CONFIG_PATH")
	if cfgPath == "" {
		cfgPath = "config/config.yaml"
	}

	cfg, err := config.Load(cfgPath)
	if err != nil {
		panic(err)
	}

	logger.Init(cfg.Log.Level, cfg.Log.Format)
	logger.L().Info("config loaded", "path", cfgPath)

	userDB, err := dbconn.Open(cfg.Database.User)
	if err != nil {
		logger.L().Error("init user db failed", "err", err)
		os.Exit(1)
	}
	defer userDB.Close()

	orderDB, err := dbconn.Open(cfg.Database.Order)
	if err != nil {
		logger.L().Error("init order db failed", "err", err)
		os.Exit(1)
	}
	defer orderDB.Close()

	userRepository := userrepo.NewSQLRepository(userDB, cfg.Database.User.Driver)
	orderRepository := orderrepo.NewSQLRepository(orderDB, cfg.Database.Order.Driver)

	var mailService notification.MailService
	if cfg.Mail.Enabled {
		mailService = mailrepo.NewSMTPMailRepository(cfg.Mail.Host, cfg.Mail.Port, cfg.Mail.Username, cfg.Mail.Password, cfg.Mail.FromEmail, cfg.Mail.FromName)
		defer mailService.Close()
	}

	var publisher event.Publisher
	var consumer event.Consumer
	if cfg.RocketMQ.Enabled {
		producer, err := mqrepo.NewProducer(cfg.RocketMQ.NameServer, cfg.RocketMQ.GroupName, cfg.RocketMQ.InstanceName, cfg.RocketMQ.RetryTimes)
		if err != nil {
			logger.L().Error("init mq producer failed", "err", err)
		} else {
			publisher = producer
			defer producer.Close()
		}

		c, err := mqrepo.NewConsumer(cfg.RocketMQ.NameServer, cfg.RocketMQ.GroupName+"-consumer", cfg.RocketMQ.InstanceName+"-consumer")
		if err != nil {
			logger.L().Error("init mq consumer failed", "err", err)
		} else {
			consumer = c
			defer c.Close()
			orderTopic := cfg.RocketMQ.Topics["order_event"]
			if err := c.Subscribe(orderTopic, func(ctx context.Context, evt event.DomainEvent) error {
				return eventsvc.HandleOrderEvent(ctx, evt, mailService)
			}); err != nil {
				logger.L().Error("subscribe mq topic failed", "topic", orderTopic, "err", err)
			}
		}
	}

	if consumer != nil {
		go func() {
			if err := consumer.Start(); err != nil {
				logger.L().Error("start mq consumer failed", "err", err)
			}
		}()
	}

	userService := usersvc.New(userRepository)
	orderService := ordersvc.New(orderRepository, userRepository, publisher, cfg.RocketMQ.Topics["order_event"])

	userController := userctl.New(userService)
	orderController := orderctl.New(orderService)

	gin.SetMode(cfg.Server.Mode)
	r := gin.New()
	r.Use(middleware.RequestID())
	r.Use(middleware.Logger())
	r.Use(middleware.Recovery())
	r.Use(middleware.CORS())

	homectl.RegisterRoutes(r)
	api := r.Group("/api")
	orderController.RegisterRoutes(api)
	userController.RegisterRoutes(api, orderController)

	addr := fmt.Sprintf("%s:%d", cfg.Server.Host, cfg.Server.Port)
	srv := &http.Server{
		Addr:         addr,
		Handler:      r,
		ReadTimeout:  time.Duration(cfg.Server.ReadTimeoutSeconds) * time.Second,
		WriteTimeout: time.Duration(cfg.Server.WriteTimeoutSeconds) * time.Second,
		IdleTimeout:  time.Duration(cfg.Server.IdleTimeoutSeconds) * time.Second,
	}

	go func() {
		logger.L().Info("server started", "addr", addr)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			logger.L().Error("server listen failed", "err", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	logger.L().Info("shutdown signal received")

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	if err := srv.Shutdown(ctx); err != nil {
		logger.L().Error("server shutdown failed", "err", err)
	}
	logger.L().Info("server exited")
}
