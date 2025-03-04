# -*- coding: utf-8 -*-
import time
import logging
from utils.logging import log_request
def auth_middleware(handler):
    def wrapper(request, response):
        start_time = time.time()
        log_message = f"Auth Checking: {request.command} {request.path}"
        logging.info("auth_middleware:", log_message)
        log_request(request, start_time)
        # 模拟授权检查，这里总是通过
        authorized = True
        if not authorized:
            response.send_response(401)
            response.send_header("Content-Type", "text/plain")
            response.end_headers()
            response.wfile.write(b"Unauthorized")
            return
        return handler(request, response)
    return wrapper