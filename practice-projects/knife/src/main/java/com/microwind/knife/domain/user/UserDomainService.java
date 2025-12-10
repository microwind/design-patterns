package com.microwind.knife.domain.user;

import com.microwind.knife.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
DomainService专注于领域内的核心业务逻辑，可处理跨多个实体的业务逻辑，DomainService 可以被不同的 Service 复用。
以下场景可使用 DomainService：
1. 跨实体协作：当业务逻辑需要操作多个领域对象时（如订单+优惠券+库存）
2. 核心算法：包含复杂计算逻辑（如订单价格计算策略）
3. 防腐败层：需要隔离外部系统对接的核心逻辑（如支付金额校验）
4. 模式复用：多个应用服务重复使用的领域逻辑
*/
@Service
@RequiredArgsConstructor
public class UserDomainService {

}