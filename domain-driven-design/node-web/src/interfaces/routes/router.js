// src/interfaces/routes/router.js
import { parse } from 'url';
export function createRouter() {
  const routes = [];

  return {
    // 注册路由
    get: (path, ...handlers) => routes.push({ method: 'GET', path, handlers }),
    post: (path, ...handlers) => routes.push({ method: 'POST', path, handlers }),
    put: (path, ...handlers) => routes.push({ method: 'PUT', path, handlers }),
    delete: (path, ...handlers) => routes.push({ method: 'DELETE', path, handlers }),

    // 处理请求
    handleRequest: (req, res) => {
      const parsedUrl = parse(req.url, true);
      const { pathname, query } = parsedUrl;

      // 查找匹配的路由
      const route = routes.find((route) => {
        if (route.method !== req.method) return false;

        // 将路径转换为正则表达式，支持 RESTful 风格的路径参数
        const routePathParts = route.path.split('/');
        const requestPathParts = pathname.split('/');

        if (routePathParts.length !== requestPathParts.length) return false;

        for (let i = 0; i < routePathParts.length; i++) {
          if (
            routePathParts[i] !== requestPathParts[i] &&
            !routePathParts[i].startsWith(':')
          ) {
            return false;
          }
        }

        return true;
      });

      if (route) {
        // 提取路径参数
        const params = {};
        const routePathParts = route.path.split('/');
        const requestPathParts = pathname.split('/');

        for (let i = 0; i < routePathParts.length; i++) {
          if (routePathParts[i].startsWith(':')) {
            const paramName = routePathParts[i].slice(1); // 去掉冒号
            params[paramName] = requestPathParts[i];
          }
        }

        // 将路径参数合并到 query 中
        const mergedQuery = { ...query, ...params };

        // 依次调用中间件和路由处理函数
        let index = 0;
        const next = () => {
          if (index < route.handlers.length) {
            const handler = route.handlers[index++];
            if (handler.length === 4) {
              // 如果是错误处理中间件
              handler(null, req, res, next);
            } else {
              // 普通中间件或路由处理函数
              handler(req, res, next, mergedQuery);
            }
          }
        };
        next();
      } else {
        // 未找到路由
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('Not Found');
      }
    },
  };
}