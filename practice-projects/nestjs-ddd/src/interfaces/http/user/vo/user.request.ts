import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsEmail,
  IsNotEmpty,
  IsOptional,
  IsString,
  Matches,
  MaxLength,
} from 'class-validator';

/**
 * 用户相关请求 VO，JSON 字段采用 snake_case，与 gin-ddd 对齐。
 */
export class CreateUserRequest {
  @ApiProperty({ example: 'jarry', description: '用户名' })
  @IsString()
  @IsNotEmpty({ message: '用户名不能为空' })
  @MaxLength(50)
  name!: string;

  @ApiProperty({ example: 'jarry@example.com', description: '邮箱' })
  @IsEmail({}, { message: '邮箱格式不正确' })
  @MaxLength(100)
  email!: string;

  @ApiPropertyOptional({ example: '13800138000', description: '手机号' })
  @IsOptional()
  @Matches(/^1[3-9]\d{9}$/, { message: '手机号格式不正确' })
  phone?: string;

  @ApiPropertyOptional({ example: '北京市朝阳区', description: '地址' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  address?: string;
}

export class UpdateEmailRequest {
  @ApiProperty({ example: 'new@example.com' })
  @IsEmail({}, { message: '邮箱格式不正确' })
  email!: string;
}

export class UpdatePhoneRequest {
  @ApiProperty({ example: '13900139000', description: '新手机号' })
  @Matches(/^1[3-9]\d{9}$/, { message: '手机号格式不正确' })
  new_phone!: string;
}
