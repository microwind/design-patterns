/**
 * Logger Implementation
 *
 * @author jarryli@gmail.com
 * @date 2024-12-20
 */

package logger

import (
  "fmt"
  "gin-order/internal/config"
  "os"
  "path/filepath"
  "runtime"
  "time"

  "github.com/sirupsen/logrus"
  "gopkg.in/natefinch/lumberjack.v2"
)

var log = logrus.New()

// FileLineHook 是一个自定义 Hook，用于自动添加 file:line
type FileLineHook struct{}

// Fire 逻辑：自动获取调用的文件名和行号
func (hook *FileLineHook) Fire(entry *logrus.Entry) error {
  _, file, line, ok := runtime.Caller(10) // 偏移量 10 适配 logrus 内部调用
  if !ok {
    file = "unknown"
    line = 0
  }
  _, fileName := filepath.Split(file)
  entry.Message = fmt.Sprintf("%s:%d | %s", fileName, line, entry.Message)
  return nil
}

// Levels 适用于所有日志级别
func (hook *FileLineHook) Levels() []logrus.Level {
  return logrus.AllLevels
}

// PlainTextFormatter 自定义格式化器，确保是纯文本输出
type PlainTextFormatter struct{}

func (f *PlainTextFormatter) Format(entry *logrus.Entry) ([]byte, error) {
  timestamp := time.Now().Format("2006-01-02 15:04:05")
  logMsg := fmt.Sprintf("%s | %s | %s\n", timestamp, entry.Level.String(), entry.Message)
  return []byte(logMsg), nil
}

// Init 初始化日志配置
func Init(cfg *config.Config) {
  log.Info("init config log:", config.GetConfig().Log)
  config := cfg.Log
  // 设置日志格式
  if config.Format == "json" {
    log.SetFormatter(&logrus.JSONFormatter{
      TimestampFormat: time.RFC3339,
    })
  } else {
    log.SetFormatter(&PlainTextFormatter{})
  }

  // 设置日志级别
  level, err := logrus.ParseLevel(config.Level)
  if err != nil {
    log.SetLevel(logrus.InfoLevel) // 默认 Info
  } else {
    log.SetLevel(level)
  }

  // 设置日志输出位置
  if config.Output == "stdout" {
    log.SetOutput(os.Stdout)
  } else if config.Output == "file" && config.File != "" {
    SetLogToFile(config.File, config.MaxSize, config.MaxBackups, config.MaxAge, config.Compress)
  }

  log.AddHook(&FileLineHook{}) // 添加文件名行号 Hook
}

// SetLogLevel 设置日志级别
func SetLogLevel(level logrus.Level) {
  log.SetLevel(level)
}

// SetLogLevelFromConfig 根据配置字符串设置日志级别
func SetLogLevelFromConfig(level string) {
  parsedLevel, err := logrus.ParseLevel(level)
  if err == nil {
    log.SetLevel(parsedLevel)
  }
}

// Debug 输出调试级别日志
func Debug(msg ...interface{}) {
  log.Debug(msg...)
}

// Info 输出普通信息级别日志
func Info(msg ...interface{}) {
  log.Info(msg...)
}

// Warn 输出警告级别日志
func Warn(msg ...interface{}) {
  log.Warn(msg...)
}

// Error 输出错误级别日志
func Error(msg ...interface{}) {
  log.Error(msg...)
}

// Printf 输出普通日志
func Printf(format string, msg ...interface{}) {
  log.Printf(format, msg...)
}

// Println 输出普通日志
func Println(msg ...interface{}) {
  log.Println(msg...)
}

// Errorf 输出带格式的错误日志
func Errorf(format string, args ...interface{}) {
  log.Errorf(format, args...)
}

// Fatal 输出致命错误日志，并停止程序
func Fatal(msg ...interface{}) {
  log.Fatal(msg...)
}

// Fatalln 输出致命错误日志，并停止程序
func Fatalln(msg ...interface{}) {
  log.Fatalln(msg...)
}

// Fatalf 输出致命错误日志，并停止程序
func Fatalf(format string, msg ...interface{}) {
  log.Fatalf(format, msg...)
}

// Panic 输出严重错误日志，并触发panic
func Panic(msg ...interface{}) {
  log.Panic(msg...)
}

// WithFields 使用字段记录日志
func WithFields(fields logrus.Fields) *logrus.Entry {
  return log.WithFields(fields)
}

// Log 返回 logrus 实例
func GetLogger() *logrus.Logger {
  return log
}

// Log 函数实现，输出文件名和行号
func Log(format string, msg ...interface{}) {
  // 获取当前的调用栈信息
  _, file, line, ok := runtime.Caller(1)
  if !ok {
    file = "unknown"
    line = 0
  }

  // 提取文件名（去掉路径部分）
  _, fileName := filepath.Split(file)

  // 组装最终日志信息
  message := fmt.Sprintf(format, msg...)
  log.Printf("%s:%d | %s", fileName, line, message)
}

// SetLogToFile 配置将日志写入指定文件，支持日志轮转
func SetLogToFile(filename string, maxSize int, maxBackups int, maxAge int, compress bool) {
  log.SetOutput(&lumberjack.Logger{
    Filename:   filename,   // 日志文件名
    MaxSize:    maxSize,    // 文件大小上限，单位 MB
    MaxBackups: maxBackups, // 保留的最大备份数
    MaxAge:     maxAge,     // 文件保存的最大天数
    Compress:   compress,   // 是否压缩旧日志
  })
  fmt.Println("日志输出到文件:", filename)
}
