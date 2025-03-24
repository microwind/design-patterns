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
  env := flag.String("env", defaultEnv, "Application environment (production, test)")
  flag.Parse()

  // 初始化配置
  config.Init(env)

  // 获取配置
  cfg := config.GetConfig()
  logger.Info("Config loaded successfully. " + cfg.Server.Addr) // 使用 logger 记录信息
  logger.Println("config:\r\n", cfg)

  // 初始化日志
  logger.Init(cfg)

  r := gin.Default()
  // 斜杠自动重定向
  r.RedirectTrailingSlash = true

  // 加载模板文件（仅作测试）[可选]
  r.LoadHTMLGlob("./web/templates/*.tmpl")

  // 路由注册
  HomeController.RegisterRoutes(r)
  InitOrder(r, cfg)

  r.Run(":8080")
}
