# -*- coding: utf-8 -*-
import time
from src.utils.logging import log_middleware
def auth_middleware(handler):
    def wrapper(request, response):
        start_time = time.time()
        # 模拟授权检查，这里总是通过
        authorized = True
        if not authorized:
            response.send_response(401)
            response.send_header("Content-Type", "text/plain")
            response.end_headers()
            response.wfile.write(b"Unauthorized 401")
            return
        log_middleware('auth_middleware', request, start_time)
        return handler(request, response)
    return wrapper