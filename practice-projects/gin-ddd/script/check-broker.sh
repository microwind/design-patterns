#!/bin/bash

# 先进到rocketmq的路径

echo "==> 1. 检查进程"
ps aux | grep mqbroker | grep -v grep

echo ""
echo "==> 2. 检查配置"
sh bin/mqadmin getBrokerConfig \
  -n 127.0.0.1:9876 \
  -b 127.0.0.1:10911 | grep diskMaxUsedSpaceRatio

echo ""
echo "==> 3. 检查集群"
sh bin/mqadmin clusterList -n 127.0.0.1:9876

echo ""
echo "==> 4. 查看最近日志"
tail -20 ~/logs/rocketmqlogs/broker.log


# 更新 Topic 配置（刷新路由信息）
# sh bin/mqadmin updateTopic \
#   -n 127.0.0.1:9876 \
#   -b 127.0.0.1:10911 \
#   -t order-event-topic

# sh bin/mqadmin updateTopic \
#   -n 127.0.0.1:9876 \
#   -b 127.0.0.1:10911 \
#   -t user-event-topic

# 查看路由信息
# sh bin/mqadmin topicRoute -n 127.0.0.1:9876 -t order-event-topic