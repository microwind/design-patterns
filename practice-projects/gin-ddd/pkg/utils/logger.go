package utils

import (
	"log"
	"os"
)

// Logger 日志工具
type Logger struct {
	infoLogger  *log.Logger
	errorLogger *log.Logger
	debugLogger *log.Logger
}

var logger *Logger

// InitLogger 初始化日志
func InitLogger() {
	logger = &Logger{
		infoLogger:  log.New(os.Stdout, "[INFO] ", log.Ldate|log.Ltime|log.Lshortfile),
		errorLogger: log.New(os.Stderr, "[ERROR] ", log.Ldate|log.Ltime|log.Lshortfile),
		debugLogger: log.New(os.Stdout, "[DEBUG] ", log.Ldate|log.Ltime|log.Lshortfile),
	}
}

// GetLogger 获取日志实例
func GetLogger() *Logger {
	if logger == nil {
		InitLogger()
	}
	return logger
}

// Info 信息日志
func (l *Logger) Info(format string, v ...interface{}) {
	l.infoLogger.Printf(format, v...)
}

// Error 错误日志
func (l *Logger) Error(format string, v ...interface{}) {
	l.errorLogger.Printf(format, v...)
}

// Debug 调试日志
func (l *Logger) Debug(format string, v ...interface{}) {
	l.debugLogger.Printf(format, v...)
}
