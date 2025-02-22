// src/interfaces/routes/router.js

export function createRouter() {
  const routes = [];

  return {
    get: (path, ...handlers) => routes.push({ method: 'GET', path, handlers }),
    post: (path, ...handlers) => routes.push({ method: 'POST', path, handlers }),
    put: (path, ...handlers) => routes.push({ method: 'PUT', path, handlers }),
    delete: (path, ...handlers) => routes.push({ method: 'DELETE', path, handlers }),

    handleRequest: (req, res) => {
      const parsedUrl = new URL(req.url, `http://${req.headers.host}`);
      const { pathname, searchParams } = parsedUrl;
      const query = Object.fromEntries(searchParams.entries());

      const route = routes.find(
        (r) => r.method === req.method && isMatchingRoute(r.path, pathname)
      );

      if (route) {
        // 解析路径参数
        const params = extractParams(route.path, pathname);
        req.query = { ...query, ...params };

        // 执行中间件和处理函数
        let index = 0;
        const next = (err) => {
          if (err) {
            while (index < route.handlers.length) {
              const handler = route.handlers[index++];
              if (handler.length === 4) {
                return handler(err, req, res, next);
              }
            }
            res.writeHead(500, { 'Content-Type': 'text/plain' });
            res.end('Internal Server Error');
            return;
          }

          if (index < route.handlers.length) {
            const handler = route.handlers[index++];
            handler(req, res, next);
          }
        };
        next();
      } else {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('Not Found');
      }
    },
  };
}

function isMatchingRoute(routePath, requestPath) {
  const routePathParts = routePath.split('/');
  const requestPathParts = requestPath.split('/');
  if (routePathParts.length !== requestPathParts.length) return false;

  return routePathParts.every((part, i) => part.startsWith(':') || part === requestPathParts[i]);
}

function extractParams(routePath, requestPath) {
  const params = {};
  const routePathParts = routePath.split('/');
  const requestPathParts = requestPath.split('/');

  routePathParts.forEach((part, i) => {
    if (part.startsWith(':')) {
      params[part.slice(1)] = requestPathParts[i];
    }
  });

  return params;
}
