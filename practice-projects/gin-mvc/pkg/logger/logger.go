package logger

import (
	"context"
	"log/slog"
	"os"
	"strings"
)

var global *slog.Logger

func Init(level, format string) {
	var handler slog.Handler

	opts := &slog.HandlerOptions{Level: parseLevel(level)}
	if strings.EqualFold(format, "json") {
		handler = slog.NewJSONHandler(os.Stdout, opts)
	} else {
		handler = slog.NewTextHandler(os.Stdout, opts)
	}

	global = slog.New(handler)
	slog.SetDefault(global)
}

func L() *slog.Logger {
	if global == nil {
		Init("info", "text")
	}
	return global
}

func With(args ...any) *slog.Logger {
	return L().With(args...)
}

func parseLevel(level string) slog.Level {
	switch strings.ToLower(level) {
	case "debug":
		return slog.LevelDebug
	case "warn":
		return slog.LevelWarn
	case "error":
		return slog.LevelError
	default:
		return slog.LevelInfo
	}
}

func Ctx(ctx context.Context) *slog.Logger {
	return L().With("request_id", requestIDFromContext(ctx))
}

func requestIDFromContext(ctx context.Context) string {
	if ctx == nil {
		return ""
	}
	v := ctx.Value("request_id")
	if s, ok := v.(string); ok {
		return s
	}
	return ""
}
