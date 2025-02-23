# -*- coding: utf-8 -*-
# 自定义路由类
import logging
import re


class Router:
    def __init__(self):
        self.routes = {
            'GET': [],
            'POST': [],
            'PUT': [],
            'DELETE': []
        }

    def add_route(self, method, path, middleware, handler):
        self.routes[method].append((path, middleware, handler))

    def get(self, path, middleware, handler):
        self.add_route('GET', path, middleware, handler)

    def post(self, path, middleware, handler):
        self.add_route('POST', path, middleware, handler)

    def put(self, path, middleware, handler):
        self.add_route('PUT', path, middleware, handler)

    def delete(self, path, middleware, handler):
        self.add_route('DELETE', path, middleware, handler)

    def match_route(self, method, path):
        for route_path, middleware, handler in self.routes[method]:
            if ':' in route_path:
                # 将路径参数替换为正则表达式，并在前后加 `^` 和 `$`
                pattern = '^' + re.sub(r':\w+', r'([^/]+)', route_path) + '$'
                try:
                    match = re.fullmatch(pattern, path)
                    if match:
                        # 提取参数并传递给 handler
                        params = match.groups()
                        return middleware, handler, params
                except re.error as e:
                    logging.error(f"正则表达式匹配出错: {e}")
            elif route_path == path:
                return middleware, handler, []

        return None, None, []

# 创建路由实例的函数
def create_router():
    return Router()
