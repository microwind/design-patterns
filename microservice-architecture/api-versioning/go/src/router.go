// Package src 实现了 API 版本管理模式（API Versioning Pattern）的核心逻辑。
//
// 支持 URL 路径版本（/v1/、/v2/）和 Header 版本（X-API-Version），提供默认版本兜底。
//
// 【设计模式】
//   - 策略模式（Strategy Pattern）：不同版本的处理器是可互换的策略，
//     路由器根据解析到的版本号选择对应策略执行。
//   - 工厂方法模式（Factory Method）：ResolveVersion 根据请求上下文决定使用哪个版本。
//   - 模板方法模式（Template Method）：Handle 定义了"解析版本 → 查找处理器 → 执行"的骨架。
//
// 【架构思想】
//   API 版本管理让新老客户端并行使用不同版本接口，实现平滑演进。
//
// 【开源对比】
//   - go-chi / gorilla/mux：通过 URL 路径参数实现版本路由
//   - grpc-go：通过 package 名或 service 名区分版本
//   本示例省略了版本协商和废弃通知等工程细节，聚焦于版本解析和路由分发。
package src

import "strings"

type Request struct {
	Path    string
	Headers map[string]string
}

type Response struct {
	StatusCode int
	Version    string
	Body       string
}

type VersionedRouter struct {
	defaultVersion string
	handlers       map[string]func() string
}

func NewVersionedRouter(defaultVersion string) *VersionedRouter {
	return &VersionedRouter{
		defaultVersion: defaultVersion,
		handlers:       map[string]func() string{},
	}
}

func (r *VersionedRouter) Register(version string, handler func() string) {
	r.handlers[normalizeVersion(version)] = handler
}

func (r *VersionedRouter) Handle(req Request) Response {
	version := r.ResolveVersion(req)
	handler := r.handlers[version]
	if handler == nil {
		return Response{
			StatusCode: 400,
			Version:    version,
			Body:       "unsupported api version",
		}
	}

	return Response{
		StatusCode: 200,
		Version:    version,
		Body:       handler(),
	}
}

func (r *VersionedRouter) ResolveVersion(req Request) string {
	path := strings.ToLower(req.Path)
	if strings.Contains(path, "/v2/") {
		return "v2"
	}
	if strings.Contains(path, "/v1/") {
		return "v1"
	}

	headerVersion := normalizeVersion(req.Headers["X-API-Version"])
	if headerVersion != "" {
		return headerVersion
	}

	return normalizeVersion(r.defaultVersion)
}

func ProductHandlerV1() string {
	return `{"id":"P100","name":"Mechanical Keyboard"}`
}

func ProductHandlerV2() string {
	return `{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}`
}

func normalizeVersion(version string) string {
	version = strings.TrimSpace(strings.ToLower(version))
	if version == "" {
		return ""
	}
	if strings.HasPrefix(version, "v") {
		return version
	}
	return "v" + version
}
