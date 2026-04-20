#!/usr/bin/env python
"""Django 管理入口。

支持通过 --yaml=path 或环境变量 CONFIG_FILE 指定配置文件。
"""
from __future__ import annotations

import os
import sys
from pathlib import Path


def main() -> None:
    base_dir = Path(__file__).resolve().parent
    src_dir = base_dir / "src"

    # 把 src/ 加入 sys.path，支持 project / user / order 作为顶级包
    sys.path.insert(0, str(src_dir))

    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "project.settings")

    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "无法导入 Django。请确认已经安装依赖，并激活虚拟环境。\n"
            "  pip install -r requirements.txt"
        ) from exc

    execute_from_command_line(sys.argv)


if __name__ == "__main__":
    main()
