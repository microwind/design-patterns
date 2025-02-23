# -*- coding: utf-8 -*-
import time


def logging_middleware(request_handler):
    def wrapper(self):
        start = time.time()

        def finish_callback():
            duration = (time.time() - start) * 1000  # 转换为毫秒
            print(f"REQUEST: {self.command} {self.path} took {duration:.2f}ms")

        original_finish = self.wfile.write

        def new_finish(data):
            result = original_finish(data)
            finish_callback()
            return result

        self.wfile.write = new_finish
        return request_handler(self)

    return wrapper