import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Put,
} from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import { CreateOrderCommand } from '../../../application/order/dto/order.commands';
import { OrderApplicationService } from '../../../application/order/order-application.service';
import { CreateOrderRequest } from './vo/order.request';
import { OrderResponse } from './vo/order.response';

/**
 * 订单 HTTP 接口，路由与 gin-ddd/internal/interfaces/router/order_router.go 对齐：
 *   POST   /api/orders
 *   GET    /api/orders
 *   GET    /api/orders/:id
 *   PUT    /api/orders/:id/pay
 *   PUT    /api/orders/:id/ship
 *   PUT    /api/orders/:id/deliver
 *   PUT    /api/orders/:id/cancel
 *   PUT    /api/orders/:id/refund
 */
@ApiTags('订单管理')
@Controller('orders')
export class OrderController {
  constructor(private readonly orderAppService: OrderApplicationService) {}

  @Post()
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '创建订单' })
  async create(@Body() req: CreateOrderRequest): Promise<OrderResponse> {
    const dto = await this.orderAppService.createOrder(
      new CreateOrderCommand(req.user_id, req.total_amount),
    );
    return OrderResponse.fromDto(dto);
  }

  @Get()
  @ApiOperation({ summary: '获取所有订单' })
  async findAll(): Promise<OrderResponse[]> {
    const dtos = await this.orderAppService.getAllOrders();
    return OrderResponse.fromDtos(dtos);
  }

  @Get(':id')
  @ApiOperation({ summary: '根据 ID 获取订单' })
  async findOne(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.getOrderById(id);
    return OrderResponse.fromDto(dto);
  }

  @Put(':id/pay')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '支付订单' })
  async pay(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.payOrder(id);
    return OrderResponse.fromDto(dto);
  }

  @Put(':id/ship')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '订单发货' })
  async ship(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.shipOrder(id);
    return OrderResponse.fromDto(dto);
  }

  @Put(':id/deliver')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '确认送达' })
  async deliver(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.deliverOrder(id);
    return OrderResponse.fromDto(dto);
  }

  @Put(':id/cancel')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '取消订单' })
  async cancel(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.cancelOrder(id);
    return OrderResponse.fromDto(dto);
  }

  @Put(':id/refund')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '订单退款' })
  async refund(@Param('id', ParseIntPipe) id: number): Promise<OrderResponse> {
    const dto = await this.orderAppService.refundOrder(id);
    return OrderResponse.fromDto(dto);
  }
}
