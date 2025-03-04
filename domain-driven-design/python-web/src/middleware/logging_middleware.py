# -*- coding: utf-8 -*-
import time
from utils.logging import log_request
import logging

import functools

def logging_middleware(handler):
    @functools.wraps(handler)
    def wrapper(self, *args, **kwargs):
        start_time = time.time()
        log_message = f"REQUEST recoder: {self.command} {self.path}"
        logging.info(f"logging_middleware: {log_message}")
        log_request(self, start_time)
        return handler(self, *args, **kwargs)
    return wrapper