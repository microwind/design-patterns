// 路由工具函数
package routes

import (
  "net/http"
  "strings"
)

// Route 定义路由结构体
type Route struct {
  Method  string
  Path    string
  Handler http.HandlerFunc
}

// Router 定义路由管理器
type Router struct {
  routes []Route
}

// NewRouter 创建一个新的路由管理器
func NewRouter() *Router {
  return &Router{
    routes: make([]Route, 0),
  }
}

// Get 注册 GET 请求的路由
func (r *Router) Get(path string, handler http.HandlerFunc) {
  r.routes = append(r.routes, Route{
    Method:  http.MethodGet,
    Path:    path,
    Handler: handler,
  })
}

// Post 注册 POST 请求的路由
func (r *Router) Post(path string, handler http.HandlerFunc) {
  r.routes = append(r.routes, Route{
    Method:  http.MethodPost,
    Path:    path,
    Handler: handler,
  })
}

// Put 注册 PUT 请求的路由
func (r *Router) Put(path string, handler http.HandlerFunc) {
  r.routes = append(r.routes, Route{
    Method:  http.MethodPut,
    Path:    path,
    Handler: handler,
  })
}

// Delete 注册 DELETE 请求的路由
func (r *Router) Delete(path string, handler http.HandlerFunc) {
  r.routes = append(r.routes, Route{
    Method:  http.MethodDelete,
    Path:    path,
    Handler: handler,
  })
}

// ServeHTTP 实现 http.Handler 接口
func (r *Router) ServeHTTP(w http.ResponseWriter, req *http.Request) {
  for _, route := range r.routes {
    if route.Method == req.Method && matchPath(route.Path, req.URL.Path) {
      route.Handler(w, req)
      return
    }
  }
  http.NotFound(w, req)
}

// matchPath 匹配路径，处理动态路由参数
func matchPath(pattern, path string) bool {
  patternParts := strings.Split(pattern, "/")
  pathParts := strings.Split(path, "/")

  if len(patternParts) != len(pathParts) {
    return false
  }

  for i, part := range patternParts {
    if strings.HasPrefix(part, ":") {
      continue
    }
    if part != pathParts[i] {
      return false
    }
  }

  return true
}
