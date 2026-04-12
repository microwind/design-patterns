/**
 * @file idempotency.c - 幂等模式（Idempotency Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 备忘录模式：用数组存储首次执行结果，后续重复请求直接返回。
 *   - 代理模式：幂等检查包裹在业务逻辑之外。
 *
 * 【架构思想】
 *   C 语言用线性查找实现幂等键匹配，fingerprint 用于冲突检测。
 *
 * 【开源对比】
 *   实际工程中幂等存储通常基于 Redis SETNX + TTL。
 *   本示例用固定数组简化。
 */

#include "func.h"

#include <stdio.h>
#include <string.h>

/** 初始化幂等服务。 */
void idempotency_service_init(IdempotencyOrderService *service)
{
    service->count = 0;
}

/**
 * 创建订单（带幂等保护）。
 *
 * 三条路径：
 *   1. 首次请求 → 执行业务 → 存储结果 → 返回 CREATED
 *   2. 重复 + 指纹匹配 → 返回存储结果（replayed=1）
 *   3. 重复 + 指纹不匹配 → 返回 CONFLICT
 */
void create_order_with_idempotency(
        IdempotencyOrderService *service,
        const char *idempotency_key,
        const char *order_id,
        const char *sku,
        int quantity,
        IdempotentOrderResponse *out_response)
{
    /* 计算请求指纹 */
    char fingerprint[128];
    snprintf(fingerprint, sizeof(fingerprint), "%s|%s|%d", order_id, sku, quantity);

    /* 遍历已存储记录，查找匹配的幂等键 */
    for (int i = 0; i < service->count; i++) {
        if (strcmp(service->records[i].idempotency_key, idempotency_key) == 0) {
            /* 同一幂等键但指纹不匹配 → 冲突 */
            if (strcmp(service->records[i].fingerprint, fingerprint) != 0) {
                strcpy(out_response->order_id, order_id);
                strcpy(out_response->sku, sku);
                out_response->quantity = quantity;
                strcpy(out_response->status, "CONFLICT");
                out_response->replayed = 0;
                return;
            }

            /* 同一幂等键且指纹匹配 → 返回存储结果 */
            *out_response = service->records[i].response;
            out_response->replayed = 1;
            return;
        }
    }

    /* 首次请求 → 执行业务逻辑 */
    strcpy(out_response->order_id, order_id);
    strcpy(out_response->sku, sku);
    out_response->quantity = quantity;
    strcpy(out_response->status, "CREATED");
    out_response->replayed = 0;

    /* 存储结果 */
    strcpy(service->records[service->count].idempotency_key, idempotency_key);
    strcpy(service->records[service->count].fingerprint, fingerprint);
    service->records[service->count].response = *out_response;
    service->count++;
}
