import os
from typing import Dict, Any

class Config:
    """配置管理类"""
    
    def __init__(self, config_path: str = None):
        self.config_path = config_path or 'config.py'
        self._config = self._load_config()
    
    def _load_config(self) -> Dict[str, Any]:
        """加载配置"""
        config = {
            'SECRET_KEY': os.getenv('SECRET_KEY', 'dev-secret-key'),
            'DEBUG': os.getenv('FLASK_DEBUG', 'True').lower() == 'true',
            'DATABASES': {
                'user': {
                    'type': 'mysql',
                    'host': os.getenv('USER_DB_HOST', 'localhost'),
                    'port': int(os.getenv('USER_DB_PORT', '3306')),
                    'username': os.getenv('USER_DB_USER', 'root'),
                    'password': os.getenv('USER_DB_PASSWORD', 'password'),
                    'database': os.getenv('USER_DB_NAME', 'flask_user_db')
                },
                'order': {
                    'type': 'mysql',
                    'host': os.getenv('ORDER_DB_HOST', 'localhost'),
                    'port': int(os.getenv('ORDER_DB_PORT', '3306')),
                    'username': os.getenv('ORDER_DB_USER', 'root'),
                    'password': os.getenv('ORDER_DB_PASSWORD', 'password'),
                    'database': os.getenv('ORDER_DB_NAME', 'flask_order_db')
                }
            },
            'EVENT_BUS_TYPE': os.getenv('EVENT_BUS_TYPE', 'memory')  # memory, redis, kafka
        }
        return config
    
    def get(self, key: str, default=None):
        """获取配置值"""
        return self._config.get(key, default)
    
    def get_database_uri(self, bind_key: str) -> str:
        """获取数据库连接URI"""
        db_config = self._config['DATABASES'].get(bind_key, {})
        if not db_config:
            raise ValueError(f"Database config not found for bind key: {bind_key}")
        
        return (
            f"mysql+pymysql://{db_config['username']}:{db_config['password']}"
            f"@{db_config['host']}:{db_config['port']}/{db_config['database']}"
        )
    
    @property
    def databases(self) -> Dict[str, Any]:
        """获取数据库配置"""
        return self._config['DATABASES']
    
    @property
    def event_bus_type(self) -> str:
        """获取事件总线类型"""
        return self._config['EVENT_BUS_TYPE']
