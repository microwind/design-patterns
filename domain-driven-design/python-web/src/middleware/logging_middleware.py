# -*- coding: utf-8 -*-
import time
from utils.logging import log_request

def logging_middleware(request, response=None, next_handler=None):
    """
    当只传入一个参数时（装饰器调用模式），
    将 request 视为处理函数 handler，返回包装后的函数；
    
    当传入三个参数时（中间件链调用模式），
    分别认为参数为 request, response, next_handler，
    并在调用 next_handler(request) 前后记录日志。
    """
    # 装饰器模式：只传入一个参数，request 实际上是 handler
    if response is None and next_handler is None:
        handler = request
        def middleware(req):
            start_time = time.time()
            res = handler(req)
            duration = (time.time() - start_time) * 1000
            log_message = f"REQUEST: {req.method} {req.path} took {duration:.2f}ms\n"
            print("logging_middleware:", log_message)
            log_request(log_message, start_time)
            return res
        return middleware

    # 中间件链模式：传入 request, response, next_handler
    start_time = time.time()
    result = next_handler(request)
    duration = (time.time() - start_time) * 1000
    log_message = f"REQUEST: {request.method} {request.path} took {duration:.2f}ms\n"
    print("logging_middleware:", log_message)
    log_request(log_message, start_time)
    return result
