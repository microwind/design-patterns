/**
 * @file func.h - 重试模式（Retry Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 策略模式：ScriptedOperation 作为策略传入 retry_run。
 *   - 模板方法模式：retry_run 定义循环调用骨架。
 *
 * 【架构思想】C 语言通过函数指针或结构体+调用函数模拟策略注入。
 */

#ifndef RETRY_PATTERN_C_FUNC_H
#define RETRY_PATTERN_C_FUNC_H

/** ScriptedOperation - 脚本化操作（测试辅助） */
typedef struct {
    int failures_before_success;  /* 成功前需要失败的次数 */
    int attempts;                 /* 当前已尝试次数 */
} ScriptedOperation;

/** 初始化脚本化操作 */
void operation_init(ScriptedOperation *operation, int failures_before_success);
/** 调用操作，返回 1=成功，0=失败 */
int operation_call(ScriptedOperation *operation);
/** 执行重试，返回 1=最终成功，0=最终失败，attempts_out 记录实际次数 */
int retry_run(int max_attempts, ScriptedOperation *operation, int *attempts_out);

#endif
