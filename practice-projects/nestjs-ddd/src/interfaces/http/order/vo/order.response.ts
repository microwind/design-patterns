import { ApiProperty } from '@nestjs/swagger';
import { OrderDto } from '../../../../application/order/dto/order.dto';
import { OrderStatus } from '../../../../domain/order/model/order.entity';

export class OrderResponse {
  @ApiProperty()
  id!: number;

  @ApiProperty({ name: 'order_no' })
  order_no!: string;

  @ApiProperty({ name: 'user_id' })
  user_id!: number;

  @ApiProperty({ name: 'total_amount' })
  total_amount!: number;

  @ApiProperty({ enum: OrderStatus })
  status!: OrderStatus;

  @ApiProperty({ name: 'created_at' })
  created_at!: Date;

  @ApiProperty({ name: 'updated_at' })
  updated_at!: Date;

  static fromDto(dto: OrderDto): OrderResponse {
    const vo = new OrderResponse();
    vo.id = dto.id;
    vo.order_no = dto.orderNo;
    vo.user_id = dto.userId;
    vo.total_amount = dto.totalAmount;
    vo.status = dto.status;
    vo.created_at = dto.createdAt;
    vo.updated_at = dto.updatedAt;
    return vo;
  }

  static fromDtos(dtos: OrderDto[]): OrderResponse[] {
    return dtos.map((d) => OrderResponse.fromDto(d));
  }
}
