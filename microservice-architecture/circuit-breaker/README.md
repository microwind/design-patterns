# circuit-breaker

该示例统一演示断路器状态机：

1. `closed`
2. 连续失败进入 `open`
3. 试探恢复进入 `half-open`
4. 成功后回到 `closed`
