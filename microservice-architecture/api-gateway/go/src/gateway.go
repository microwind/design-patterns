// Package src 实现了 API 网关模式（API Gateway Pattern）的核心逻辑。
//
// API 网关是微服务架构的统一入口，负责请求路由、中间件处理和跨切面关注点。
//
// 【设计模式】
//   - 外观模式（Facade Pattern）：网关为客户端提供统一入口，屏蔽后端服务拆分细节。
//   - 责任链模式（Chain of Responsibility）：中间件按注册顺序依次执行，
//     任一中间件可拦截请求并直接返回响应。
//   - 策略模式（Strategy Pattern）：Handler 和 Middleware 作为函数类型，
//     不同的处理器和中间件是可插拔的策略。
//
// 【架构思想】
//   API 网关集中处理认证、限流、日志、链路追踪等跨切面关注点。
//
// 【开源对比】
//   - Traefik（Go 编写）：云原生反向代理和网关，支持自动服务发现
//   - KrakenD（Go 编写）：高性能 API 网关，支持聚合和转换
//   本示例省略了反向代理、动态路由等工程细节，聚焦于路由匹配和中间件链。
package src

import "strings"

type Request struct {
	Method  string
	Path    string
	Headers map[string]string
}

type Response struct {
	StatusCode int
	Body       string
	Headers    map[string]string
}

type Handler func(Request) Response
type Middleware func(Request) *Response

type APIGateway struct {
	routes      map[string]Handler
	middlewares []Middleware
}

func NewAPIGateway() *APIGateway {
	return &APIGateway{
		routes:      map[string]Handler{},
		middlewares: []Middleware{},
	}
}

func (g *APIGateway) Use(m Middleware) {
	g.middlewares = append(g.middlewares, m)
}

func (g *APIGateway) Register(prefix string, handler Handler) {
	g.routes[prefix] = handler
}

func (g *APIGateway) Handle(req Request) Response {
	for _, middleware := range g.middlewares {
		if response := middleware(req); response != nil {
			return *response
		}
	}

	_, handler := g.match(req.Path)
	if handler == nil {
		return Response{StatusCode: 404, Body: "gateway: route not found", Headers: map[string]string{}}
	}

	response := handler(req)
	if response.Headers == nil {
		response.Headers = map[string]string{}
	}

	correlationID := req.Headers["X-Correlation-ID"]
	if correlationID == "" {
		correlationID = "gw-generated-correlation-id"
	}
	response.Headers["X-Correlation-ID"] = correlationID

	return response
}

func (g *APIGateway) match(path string) (string, Handler) {
	var matchedPrefix string
	var matchedHandler Handler

	for prefix, handler := range g.routes {
		if strings.HasPrefix(path, prefix) && len(prefix) > len(matchedPrefix) {
			matchedPrefix = prefix
			matchedHandler = handler
		}
	}

	return matchedPrefix, matchedHandler
}

func RequireUserHeader(prefix string, headerName string) Middleware {
	return func(req Request) *Response {
		if !strings.HasPrefix(req.Path, prefix) {
			return nil
		}

		if req.Headers[headerName] == "" {
			return &Response{
				StatusCode: 401,
				Body:       "gateway: unauthorized",
				Headers:    map[string]string{},
			}
		}

		return nil
	}
}

func OrderServiceHandler() Handler {
	return func(req Request) Response {
		return Response{
			StatusCode: 200,
			Body:       "order-service handled " + req.Path,
			Headers:    map[string]string{"X-Upstream-Service": "order-service"},
		}
	}
}

func InventoryServiceHandler() Handler {
	return func(req Request) Response {
		return Response{
			StatusCode: 200,
			Body:       "inventory-service handled " + req.Path,
			Headers:    map[string]string{"X-Upstream-Service": "inventory-service"},
		}
	}
}
