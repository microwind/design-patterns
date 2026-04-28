import logging
import json
from logging.handlers import RotatingFileHandler
import os


def get_logger(name: str) -> logging.Logger:
    """获取已配置的日志记录器实例"""
    logger = logging.getLogger(name)
    logger.setLevel(logging.INFO)
    
    # 防止重复添加处理器
    if logger.handlers:
        return logger
    
    # 控制台处理器
    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)
    
    # JSON 格式化器，用于结构化日志
    class JsonFormatter(logging.Formatter):
        def format(self, record):
            log_data = {
                'timestamp': self.formatTime(record),
                'level': record.levelname,
                'logger': record.name,
                'message': record.getMessage(),
            }
            if hasattr(record, 'request_id'):
                log_data['request_id'] = record.request_id
            if record.exc_info:
                log_data['exception'] = self.formatException(record.exc_info)
            return json.dumps(log_data)
    
    formatter = JsonFormatter()
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)
    
    return logger
