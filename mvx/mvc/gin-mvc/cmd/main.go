package main

import (
  "flag"
  "gin-order/internal/config"
  HomeController "gin-order/internal/controllers/home"
  OrderController "gin-order/internal/controllers/order"
  "gin-order/internal/repository"
  OrderRepository "gin-order/internal/repository/order"
  OrderService "gin-order/internal/services/order"
  "gin-order/pkg/logger"
  "os"

  "github.com/gin-gonic/gin"
)

func InitOrder(r *gin.Engine, config *config.Config) {
  // 初始化order相关
  dbConfig := (*repository.DatabaseConfig)(&config.Database)
  // gorm_connection连接数据库
  db, err := repository.ConnectGormDB(dbConfig)
  // mysq_connection连接数据库
  // db, err := repository.ConnectMySQL(dbConfig)
  if err != nil {
    logger.Error("Failed to connect to database: ", err)
    return
  }
  orderRepo := OrderRepository.NewOrderGormRepository(db) // gorm_connection

  // orderRepo := OrderRepository.NewOrderMySQLRepository(db) // mysq_connection
  orderService := OrderService.NewOrderServiceImpl(orderRepo)
  orderController := OrderController.NewOrderController(orderService)
  orderController.RegisterRoutes(r)
}

func main() {
  gin.SetMode(gin.ReleaseMode)       // 强制切换为生产模式[1,5](@ref)
  defaultEnv := os.Getenv("APP_ENV") // 读取系统环境变量，默认test
  // 解析命令行参数
  env := flag.String("env", defaultEnv, "Application environment (production, test, dev)")
  flag.Parse()

  // 初始化配置
  config.Init(env)

  // 获取配置
  cfg := config.GetConfig()

  // 初始化日志
  logger.Init(cfg)

  logger.Info("Config loaded successfully. " + cfg.Server.Addr) // 使用 logger 记录信息
  logger.Println("config:\r\n", cfg)

  // r := gin.Default()

  r := gin.New() // 不使用 gin.Default()，避免默认日志污染 logrus
  // 替换 Gin 日志
  r.Use(gin.LoggerWithConfig(gin.LoggerConfig{
    Output: logger.GetLogger().Out, // 让 Gin 访问日志输出到 logrus
  }))
  r.Use(gin.Recovery())

  // 斜杠自动重定向
  r.RedirectTrailingSlash = true

  // 加载模板文件（仅作测试）[可选]
  r.LoadHTMLGlob("./web/templates/*.tmpl")

  // 路由注册
  HomeController.RegisterRoutes(r)
  InitOrder(r, cfg)

  r.Run(":8080")
}
