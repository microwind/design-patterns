// 实用工具
package utils

import (
  "log"
  "net/http"
  "os"
  "path/filepath"
  "time"
)

func SetupLogging(logFile string) {
  // 确保日志目录存在
  logDir := filepath.Dir(logFile) // 获取文件路径的目录部分
  if err := os.MkdirAll(logDir, 0755); err != nil {
    log.Fatalf("无法创建日志目录 %s: %v", logDir, err)
  }

  // 打开或创建日志文件
  file, err := os.OpenFile(logFile, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0644)
  if err != nil {
    log.Fatalf("无法打开日志文件 %s: %v", logFile, err)
  }
  defer file.Close() // 确保函数退出时关闭文件

  // 设置 log 包默认输出到文件
  log.SetOutput(file)
  log.Println("日志系统初始化完成，日志写入:", logFile)
}

// LogPrintf 打印请求日志
func LogPrintf(r *http.Request, start time.Time) {
  log.Printf("REQUEST: %s %s took %v", r.Method, r.URL.Path, time.Since(start))
}

// LogInfo 打印普通信息日志
func LogInfo(message string) {
  log.Println("INFO: " + message)
}

// LogError 打印错误信息日志
func LogError(message string) {
  log.Println("ERROR: " + message)
}
