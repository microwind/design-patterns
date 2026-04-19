import { User } from '../../../domain/user/model/user.entity';

/** 应用层 ↔ 接口层传递的用户数据结构（内部 camelCase） */
export class UserDto {
  id!: number;
  name!: string;
  email!: string;
  phone?: string;
  address?: string;
  createdTime!: Date;
  updatedTime!: Date;

  static fromEntity(user: User): UserDto {
    const dto = new UserDto();
    dto.id = user.id!;
    dto.name = user.name;
    dto.email = user.email;
    dto.phone = user.phone;
    dto.address = user.address;
    dto.createdTime = user.createdTime;
    dto.updatedTime = user.updatedTime;
    return dto;
  }

  static fromEntities(users: User[]): UserDto[] {
    return users.map((u) => UserDto.fromEntity(u));
  }
}
