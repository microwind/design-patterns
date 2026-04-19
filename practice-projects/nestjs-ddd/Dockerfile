# syntax=docker/dockerfile:1.7
# 多阶段构建：先编译再运行，保持镜像小巧。

# ---------- 构建阶段 ----------
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install --no-audit --no-fund

COPY tsconfig*.json nest-cli.json ./
COPY src ./src
RUN npm run build

# ---------- 运行阶段 ----------
FROM node:20-alpine AS runner

WORKDIR /app
ENV NODE_ENV=production

COPY package*.json ./
RUN npm install --omit=dev --no-audit --no-fund && npm cache clean --force

COPY --from=builder /app/dist ./dist
COPY config ./config

EXPOSE 8080

CMD ["node", "dist/main.js"]
