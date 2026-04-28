import logging
import json
from logging.handlers import RotatingFileHandler
import os


def get_logger(name: str) -> logging.Logger:
    """Get configured logger instance"""
    logger = logging.getLogger(name)
    logger.setLevel(logging.INFO)
    
    # Prevent duplicate handlers
    if logger.handlers:
        return logger
    
    # Console handler
    console_handler = logging.StreamHandler()
    console_handler.setLevel(logging.INFO)
    
    # JSON formatter for structured logging
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
