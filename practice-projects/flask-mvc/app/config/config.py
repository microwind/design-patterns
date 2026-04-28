import os
import yaml
from pathlib import Path


class Config:
    """Application configuration loaded from YAML file"""

    def __init__(self, config_path: str = None):
        if config_path is None:
            config_path = os.path.join(
                Path(__file__).parent.parent.parent, 'config', 'config.yaml'
            )
        
        with open(config_path, 'r', encoding='utf-8') as f:
            self.config_data = yaml.safe_load(f)

    @property
    def server(self) -> dict:
        return self.config_data.get('server', {})

    @property
    def database(self) -> dict:
        return self.config_data.get('database', {})

    @property
    def log(self) -> dict:
        return self.config_data.get('log', {})

    @property
    def celery(self) -> dict:
        return self.config_data.get('celery', {})

    @property
    def mail(self) -> dict:
        return self.config_data.get('mail', {})

    def get_database_uri(self, db_name: str) -> str:
        """Get database URI for given database name (user or order)"""
        db_config = self.database.get(db_name, {})
        driver = db_config.get('driver', 'mysql')
        
        if driver == 'mysql':
            return (
                f"mysql+pymysql://{db_config.get('username')}:"
                f"{db_config.get('password')}@{db_config.get('host')}:"
                f"{db_config.get('port')}/{db_config.get('database')}"
                f"?charset=utf8mb4"
            )
        elif driver == 'postgresql':
            return (
                f"postgresql://{db_config.get('username')}:"
                f"{db_config.get('password')}@{db_config.get('host')}:"
                f"{db_config.get('port')}/{db_config.get('database')}"
            )
        else:
            raise ValueError(f"Unsupported database driver: {driver}")
