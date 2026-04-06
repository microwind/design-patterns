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
