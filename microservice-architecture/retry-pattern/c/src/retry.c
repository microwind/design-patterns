/**
 * @file retry.c - 重试模式（Retry Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 策略模式：operation 作为参数传入 retry_run。
 *   - 模板方法模式：retry_run 定义循环调用骨架。
 *
 * 【架构思想】
 *   C 语言中重试逻辑通常嵌入在网络库的连接/发送函数中。
 *   libcurl 的 CURLOPT_RETRY 选项提供内置重试支持。
 *
 * 【开源对比】
 *   - libcurl：CURLOPT_RETRY + CURLOPT_RETRY_DELAY
 *   - Nginx：proxy_next_upstream 自动重试下一个 upstream
 *   本示例实现固定次数重试。
 */

#include "func.h"

/** 初始化脚本化操作 */
void operation_init(ScriptedOperation *operation, int failures_before_success)
{
    operation->failures_before_success = failures_before_success;
    operation->attempts = 0;
}

/** 调用操作。前 failures_before_success 次返回 0（失败）。 */
int operation_call(ScriptedOperation *operation)
{
    operation->attempts++;
    return operation->attempts > operation->failures_before_success;
}

/**
 * 执行重试。循环调用操作，成功时立即返回。
 * @param max_attempts  最大尝试次数
 * @param operation     待重试操作
 * @param attempts_out  输出实际尝试次数
 * @return 1=最终成功，0=最终失败
 */
int retry_run(int max_attempts, ScriptedOperation *operation, int *attempts_out)
{
    for (int attempt = 1; attempt <= max_attempts; attempt++) {
        /* 调用操作，成功则立即返回 */
        if (operation_call(operation)) {
            *attempts_out = attempt;
            return 1;
        }
    }
    /* 达到最大次数仍失败 */
    *attempts_out = max_attempts;
    return 0;
}
