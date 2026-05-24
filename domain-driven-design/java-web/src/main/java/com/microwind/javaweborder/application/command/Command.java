// 应用层(Application) - 命令对象基础
//
// CQRS 入门形态：命令 (Command) 表达"想做某件事"（写意图），
// 查询 (Query) 表达"想看某些数据"（纯读）。
//
// 用 Command 取代多参数的好处：签名稳定、意图显式、便于序列化。
package com.microwind.javaweborder.application.command;

// 标识接口：便于统一处理（日志、审计、追踪）
public interface Command {
}
