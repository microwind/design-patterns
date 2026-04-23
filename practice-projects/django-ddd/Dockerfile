# syntax=docker/dockerfile:1.7
# django-ddd 生产镜像：默认走 PyMySQL + psycopg[binary]，整机零系统依赖。
# 如需切换到性能更好的 mysqlclient，在 requirements.txt 开启该包，并取消下方
# apt-get 安装 `default-libmysqlclient-dev pkg-config build-essential` 的注释。

FROM python:3.12-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

WORKDIR /app

# 默认构建不需要编译链；如启用 mysqlclient，请解除下面的注释：
# RUN apt-get update \
#  && apt-get install -y --no-install-recommends \
#         build-essential default-libmysqlclient-dev pkg-config \
#  && rm -rf /var/lib/apt/lists/*

COPY requirements.txt ./
RUN pip install --upgrade pip \
 && pip install -r requirements.txt

COPY . .

EXPOSE 8080

# 启动脚本：先应用 migrate，再拉起 gunicorn
RUN pip install "gunicorn>=22.0"

CMD ["sh", "-c", "exec gunicorn --chdir src project.wsgi:application --bind 0.0.0.0:8080 --workers 3 --access-logfile -"]
