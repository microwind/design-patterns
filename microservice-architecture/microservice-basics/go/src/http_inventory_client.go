package src

import (
	"fmt"
	"io"
	"net/http"
	"net/url"
)

// HttpInventoryClient 是 HTTP 远程库存客户端（阶段2）。
//
// 【设计模式】
//   - 适配器模式（Adapter Pattern）：将 HTTP 远程调用适配为 InventoryClient 接口，
//     调用方（OrderService）无需感知底层是 HTTP 通信。
//   - 代理模式（Proxy Pattern）：作为远程库存服务的本地代理。
//
// 【架构思想】
//   当库存服务独立部署后，订单服务通过 HTTP 客户端调用远程接口。
//   "远程调用不能当成本地函数调用"——需要处理网络超时、连接失败等问题。
//
// 【开源对比】
//   - gRPC-Go：基于 HTTP/2 + Protobuf 的高性能客户端
//   - go-resty / net/http：HTTP 客户端库
//   本示例使用 Go 标准库 net/http，展示最基础的 HTTP 通信。
type HttpInventoryClient struct {
	BaseURL string       // 库存服务基础 URL
	Client  *http.Client // HTTP 客户端
}

// NewHttpInventoryClient 创建 HTTP 库存客户端。
func NewHttpInventoryClient(baseURL string) *HttpInventoryClient {
	return &HttpInventoryClient{
		BaseURL: baseURL,
		Client:  &http.Client{},
	}
}

// Reserve 通过 HTTP GET 调用远程库存服务进行库存预留。
// 返回 true 表示预留成功（HTTP 200 + 响应体 "OK"），否则返回 false。
func (c *HttpInventoryClient) Reserve(sku string, quantity int) bool {
	// 构建请求 URL，对 SKU 进行 URL 编码
	endpoint := fmt.Sprintf("%s/reserve?sku=%s&quantity=%d", c.BaseURL, url.QueryEscape(sku), quantity)
	// 发送 HTTP GET 请求
	resp, err := c.Client.Get(endpoint)
	if err != nil {
		// 网络异常（连接失败、超时等），返回失败
		return false
	}
	defer resp.Body.Close()

	// 读取响应体
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return false
	}

	// 判断预留是否成功：HTTP 200 且响应体为 "OK"
	return resp.StatusCode == http.StatusOK && string(body) == "OK"
}
