# -*- coding: utf-8 -*-
import time
from src.utils.logging import log_middleware
import functools

def logging_middleware(handler):
    @functools.wraps(handler)
    def wrapper(self, *args, **kwargs):
        start_time = time.time()
        log_middleware('logging_middleware', self, start_time)
        return handler(self, *args, **kwargs)
    return wrapper