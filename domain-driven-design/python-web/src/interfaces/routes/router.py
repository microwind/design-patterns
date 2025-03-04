# -*- coding: utf-8 -*-
import logging
import re

# 自定义路由工具
class Router:
    def __init__(self):
        self.routes = {
            'GET': [],
            'POST': [],
            'PUT': [],
            'DELETE': []
        }

    def add_route(self, method, path, *handlers):
        if not handlers:
            raise ValueError("必须至少提供一个处理函数")
        # 最后一个参数是真正的处理函数
        handler = handlers[-1]
        # 前面的参数是中间件
        middlewares = handlers[:-1]
        # 按顺序应用中间件（先提供的中间件最先执行）
        for middleware in reversed(middlewares):
            handler = middleware(handler)
        self.routes[method].append((path, handler))

    def get(self, path, *handlers):
        self.add_route('GET', path, *handlers)

    def post(self, path, *handlers):
        self.add_route('POST', path, *handlers)

    def put(self, path, *handlers):
        self.add_route('PUT', path, *handlers)

    def delete(self, path, *handlers):
        self.add_route('DELETE', path, *handlers)

    def match_route(self, method, path):
        if method not in self.routes:
            return None

        for route_path, handler in self.routes[method]:
            if ':' in route_path:
                # 将路径参数替换为正则表达式，并在前后加上 ^ 和 $
                pattern = '^' + re.sub(r':(\w+)', r'(?P<\1>[^/]+)', route_path) + '$'
                try:
                    match = re.fullmatch(pattern, path)
                    if match:
                        # 提取参数并存储到 request.params
                        params = match.groupdict()
                        # 包装处理函数，先将参数写入 request 对象
                        def wrapped_handler(req, resp, h=handler, params=params):
                            setattr(req, 'params', params)
                            return h(req, resp)  # 传入 request 和 response
                        return wrapped_handler
                except re.error as e:
                    logging.error(f"正则表达式匹配出错: {e}")
            elif route_path == path:
                # 对于无参数的静态路由，同样包装后调用 h(req, resp)
                def wrapped_handler(req, resp, h=handler):
                    return h(req, resp)
                return wrapped_handler

        return None

# 创建路由实例的函数
def create_router():
    return Router()
