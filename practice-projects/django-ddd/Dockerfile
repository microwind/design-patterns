# syntax=docker/dockerfile:1.7
# django-ddd 生产镜像：基于 python slim，预装 MySQL / Postgres 客户端编译依赖

FROM python:3.12-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

WORKDIR /app

# mysqlclient 需要编译 mysql 头文件；psycopg[binary] 不需要额外系统依赖
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential \
        default-libmysqlclient-dev \
        pkg-config \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt ./
RUN pip install --upgrade pip \
 && pip install -r requirements.txt

COPY . .

EXPOSE 8080

# 启动脚本：先应用 migrate，再拉起 gunicorn
RUN pip install "gunicorn>=22.0"

CMD ["sh", "-c", "exec gunicorn --chdir src project.wsgi:application --bind 0.0.0.0:8080 --workers 3 --access-logfile -"]
