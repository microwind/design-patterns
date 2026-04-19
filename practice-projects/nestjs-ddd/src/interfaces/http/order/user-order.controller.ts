import { Controller, Get, Param, ParseIntPipe } from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { OrderApplicationService } from '../../../application/order/order-application.service';
import { OrderResponse } from './vo/order.response';

/**
 * 用户-订单聚合查询接口（跨聚合），对应 gin-ddd 的
 *   GET /api/users/:id/orders
 *
 * 放在订单模块内，使用订单应用服务，不需要依赖 UserModule 中的 UserApplicationService。
 */
@ApiTags('订单管理')
@Controller('users')
export class UserOrderController {
  constructor(private readonly orderAppService: OrderApplicationService) {}

  @Get(':id/orders')
  @ApiOperation({ summary: '获取用户的所有订单' })
  async findUserOrders(
    @Param('id', ParseIntPipe) userId: number,
  ): Promise<OrderResponse[]> {
    const dtos = await this.orderAppService.getUserOrders(userId);
    return OrderResponse.fromDtos(dtos);
  }
}
