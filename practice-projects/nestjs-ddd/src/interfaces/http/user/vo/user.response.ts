import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { UserDto } from '../../../../application/user/dto/user.dto';

/**
 * 用户接口响应 VO - JSON 字段采用 snake_case，与 gin-ddd 对齐。
 */
export class UserResponse {
  @ApiProperty()
  id!: number;

  @ApiProperty()
  name!: string;

  @ApiProperty()
  email!: string;

  @ApiPropertyOptional()
  phone?: string;

  @ApiPropertyOptional()
  address?: string;

  @ApiProperty({ name: 'created_time' })
  created_time!: Date;

  @ApiProperty({ name: 'updated_time' })
  updated_time!: Date;

  static fromDto(dto: UserDto): UserResponse {
    const vo = new UserResponse();
    vo.id = dto.id;
    vo.name = dto.name;
    vo.email = dto.email;
    vo.phone = dto.phone;
    vo.address = dto.address;
    vo.created_time = dto.createdTime;
    vo.updated_time = dto.updatedTime;
    return vo;
  }

  static fromDtos(dtos: UserDto[]): UserResponse[] {
    return dtos.map((d) => UserResponse.fromDto(d));
  }
}
