# utils/middleware.py
from flask import request
import time

def init_middleware(app):
    @app.before_request
    def log_request_info():
        request.start_time = time.time()
        print(f"[{request.method}] {request.path} - Started")

    @app.after_request
    def log_response_info(response):
        duration = round((time.time() - request.start_time) * 1000, 2)
        print(f"[{request.method}] {request.path} - Completed in {duration}ms")
        return response