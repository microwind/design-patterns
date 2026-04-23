"""Django project 启动钩子。

默认使用 PyMySQL 作为 MySQL 驱动（纯 Python，无需 libmysqlclient / pkg-config），
在这里注册为 MySQLdb，让 Django 的 `django.db.backends.mysql` 无缝使用。

Django 的 MySQL 后端会校验 `MySQLdb.version_info >= (1, 4, 3)`，而 PyMySQL 自带的
`VERSION` 往往低于这个值，因此需要额外覆写，否则 Django 启动会 ImproperlyConfigured。

当环境中同时装了原生的 `mysqlclient`（C 扩展，性能更好）时，优先使用 mysqlclient，
此处的 shim 不再注册，行为与标准 Django 一致。
"""
from __future__ import annotations


def _install_mysql_driver() -> None:
    try:
        import MySQLdb  # noqa: F401  原生 mysqlclient，已可用
        return
    except ImportError:
        pass

    try:
        import pymysql
    except ImportError:
        return

    pymysql.install_as_MySQLdb()

    import MySQLdb  # 这里的 MySQLdb 实际上是 pymysql

    MySQLdb.version_info = (1, 4, 6, "final", 0)
    MySQLdb.__version__ = "1.4.6"


_install_mysql_driver()
