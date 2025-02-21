package middleware

import (
  "go-order-system/pkg/utils"
  "net/http"
  "time"
)

// LoggingMiddleware 记录请求日志的中间件
func LoggingMiddleware(next http.HandlerFunc) http.HandlerFunc {
  return func(w http.ResponseWriter, r *http.Request) {
    start := time.Now()
    // 继续处理请求
    next(w, r)
    // 记录日志
    // log.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    utils.LogPrintf(r, start)
  }
}
