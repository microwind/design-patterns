package utils

import (
  "encoding/json"
  "fmt"
  "net/http"
  "os"
)

// 发送标准响应
func SendResponse(w http.ResponseWriter, statusCode int, data interface{}, contentType string, headers map[string]string) {
  if contentType == "" {
    contentType = "application/json"
  }

  // 设置响应头
  w.Header().Set("Content-Type", contentType)
  for key, value := range headers {
    w.Header().Set(key, value)
  }

  // 根据不同的 contentType 处理响应内容
  switch contentType {
  case "application/json":
    w.WriteHeader(statusCode)
    _ = json.NewEncoder(w).Encode(data)
  case "text/plain":
    w.WriteHeader(statusCode)
    w.Write([]byte(fmt.Sprintf("%v", data)))
  case "application/xml", "text/html", "application/octet-stream":
    w.WriteHeader(statusCode)
    w.Write([]byte(fmt.Sprintf("%v", data)))
  default:
    w.WriteHeader(statusCode)
    w.Write([]byte(fmt.Sprintf("%v", data)))
  }
}

// 发送错误响应
func SendError(w http.ResponseWriter, statusCode int, message string, contentType string, headers map[string]string) {
  if contentType == "" {
    contentType = "application/json"
  }

  // 设置响应头
  w.Header().Set("Content-Type", contentType)
  for key, value := range headers {
    w.Header().Set(key, value)
  }

  // 错误响应体
  var errorResponse interface{}
  if contentType == "application/json" {
    errorResponse = map[string]interface{}{
      "error": message,
    }
  } else {
    errorResponse = message
  }

  // 根据 contentType 返回不同的响应内容
  switch contentType {
  case "application/json":
    w.WriteHeader(statusCode)
    _ = json.NewEncoder(w).Encode(errorResponse)
  default:
    w.WriteHeader(statusCode)
    w.Write([]byte(fmt.Sprintf("%v", errorResponse)))
  }
}

// 发送空响应（204 No Content）
func SendNoContent(w http.ResponseWriter) {
  w.WriteHeader(http.StatusNoContent)
  w.Write([]byte{})
}

// 发送文件
func SendFile(w http.ResponseWriter, filePath string, fileName string, contentType string) {
  if contentType == "" {
    contentType = "application/octet-stream"
  }

  file, err := os.Open(filePath)
  if err != nil {
    SendError(w, http.StatusInternalServerError, "无法读取文件", "application/json", nil)
    return
  }
  defer file.Close()

  fileStats, err := file.Stat()
  if err != nil {
    SendError(w, http.StatusInternalServerError, "无法获取文件信息", "application/json", nil)
    return
  }

  // 设置响应头
  w.Header().Set("Content-Type", contentType)
  w.Header().Set("Content-Disposition", fmt.Sprintf("attachment; filename=\"%s\"", fileName))
  w.Header().Set("Content-Length", fmt.Sprintf("%d", fileStats.Size()))

  // 传输文件内容
  http.ServeFile(w, nil, filePath)
}

// 设置缓存头
func SetCacheHeaders(w http.ResponseWriter, cacheDuration int) {
  expirationDate := fmt.Sprintf("max-age=%d", cacheDuration)
  w.Header().Set("Cache-Control", expirationDate)
}

// 设置 CORS 头
func SetCorsHeaders(w http.ResponseWriter, origin string) {
  if origin == "" {
    origin = "*"
  }
  w.Header().Set("Access-Control-Allow-Origin", origin)
  w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
  w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
}
