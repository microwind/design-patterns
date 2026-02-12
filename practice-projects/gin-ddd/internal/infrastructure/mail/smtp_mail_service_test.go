package mail

import (
	"bytes"
	"context"
	"testing"
)

func TestGetOrderConfirmationTemplate(t *testing.T) {
	tmpl := GetOrderConfirmationTemplate()
	if tmpl == nil {
		t.Fatal("期望获取邮件模板，但返回了 nil")
	}

	// 测试模板是否可以成功渲染
	data := OrderConfirmationMailData{
		UserName:    "张三",
		OrderNo:     "ORD20240101123456",
		OrderID:     123456,
		TotalAmount: 999.99,
		Status:      "PENDING",
	}

	var buf bytes.Buffer
	if err := tmpl.Execute(&buf, data); err != nil {
		t.Fatalf("模板渲染失败: %v", err)
	}

	// 检查渲染结果中是否包含关键信息
	result := buf.String()
	if result == "" {
		t.Fatal("期望渲染结果不为空，但返回了空字符串")
	}
	if !contains(result, "张三") {
		t.Fatal("期望邮件内容包含用户名")
	}
	if !contains(result, "ORD20240101123456") {
		t.Fatal("期望邮件内容包含订单号")
	}
	if !contains(result, "999.99") {
		t.Fatal("期望邮件内容包含订单金额")
	}
}

func TestIsValidEmail(t *testing.T) {
	tests := []struct {
		email string
		valid bool
	}{
		{"user@example.com", true},
		{"test@test.co", true},
		{"admin@company.org", true},
		{"", false},
		{"invalid-email", false},
		{"no-at-sign.com", false},
	}

	for _, tt := range tests {
		result := isValidEmail(tt.email)
		if result != tt.valid {
			t.Errorf("isValidEmail(%q) = %v, 期望 %v", tt.email, result, tt.valid)
		}
	}
}

func TestToInt64(t *testing.T) {
	tests := []struct {
		input    interface{}
		expected int64
	}{
		{int64(123), int64(123)},
		{int(456), int64(456)},
		{float64(789), int64(789)},
		{"invalid", int64(0)},
	}

	for _, tt := range tests {
		result := toInt64(tt.input)
		if result != tt.expected {
			t.Errorf("toInt64(%v) = %v, 期望 %v", tt.input, result, tt.expected)
		}
	}
}

func TestToFloat64(t *testing.T) {
	tests := []struct {
		input    interface{}
		expected float64
	}{
		{float64(123.45), 123.45},
		{int(456), 456.0},
		{int64(789), 789.0},
		{"invalid", 0.0},
	}

	for _, tt := range tests {
		result := toFloat64(tt.input)
		if result != tt.expected {
			t.Errorf("toFloat64(%v) = %v, 期望 %v", tt.input, result, tt.expected)
		}
	}
}

func TestNewSMTPMailService(t *testing.T) {
	service := NewSMTPMailService(
		"smtp.example.com",
		587,
		"user@example.com",
		"password",
		"sender@example.com",
		"Sender Name",
	)

	if service == nil {
		t.Fatal("期望创建邮件服务，但返回了 nil")
	}

	// 测试 Close 方法
	if err := service.Close(); err != nil {
		t.Fatalf("关闭邮件服务失败: %v", err)
	}
}

func TestSendOrderConfirmationMailWithInvalidEmail(t *testing.T) {
	service := NewSMTPMailService(
		"smtp.example.com",
		587,
		"user@example.com",
		"password",
		"sender@example.com",
		"Sender Name",
	)

	ctx := context.Background()
	orderData := map[string]interface{}{
		"order_id":     int64(123),
		"order_no":     "ORD123",
		"total_amount": 99.99,
		"status":       "PENDING",
	}

	// 测试无效邮箱
	err := service.SendOrderConfirmationMail(ctx, "invalid-email", "用户", orderData)
	if err == nil {
		t.Fatal("期望无效邮箱返回错误")
	}
}

// 辅助函数
func contains(str, substr string) bool {
	for i := 0; i < len(str)-len(substr)+1; i++ {
		if str[i:i+len(substr)] == substr {
			return true
		}
	}
	return false
}
