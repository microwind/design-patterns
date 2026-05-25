package com.microwind.javaweborder.application.command;

/**
 * 命令对象标识接口。
 *
 * <p>DDD/CQRS 实践：<b>命令（Command）</b>表达"想做某件事"（写意图），
 * <b>查询（Query）</b>表达"想看某些数据"（读，纯函数）。两者分别承载写、读两条路径。
 *
 * <h3>为什么用 Command 取代多参数</h3>
 * <ul>
 *   <li>签名稳定：演化时仅扩 Command 字段，应用服务方法签名不变</li>
 *   <li>意图显式：一个 Command 类对应一个明确的业务用例</li>
 *   <li>便于序列化：可直接投递到消息队列、Job 队列、审计日志</li>
 * </ul>
 *
 * <p>实现本接口仅用于类型标识，便于统一处理（日志、审计、追踪）。
 */
public interface Command {
}
