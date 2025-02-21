// 实用工具
package utils

import (
  "log"
  "net/http"
  "time"
)

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
