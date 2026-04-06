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
