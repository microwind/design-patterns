import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsPositive, Min } from 'class-validator';

/**
 * 创建订单请求（snake_case JSON，与 gin-ddd 对齐）。
 * 属性名直接使用 snake_case，无需 @Expose。
 */
export class CreateOrderRequest {
  @ApiProperty({ example: 1, description: '用户 ID' })
  @IsInt()
  @IsPositive({ message: '用户 ID 无效' })
  user_id!: number;

  @ApiProperty({ example: 99.9, description: '订单总金额' })
  @Min(0.01, { message: '订单金额必须大于 0' })
  total_amount!: number;
}
