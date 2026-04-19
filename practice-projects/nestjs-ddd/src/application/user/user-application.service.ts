import { ConflictException, Inject, Injectable, Logger, NotFoundException } from '@nestjs/common';
import { User } from '../../domain/user/model/user.entity';
import {
  USER_REPOSITORY,
  UserRepository,
} from '../../domain/user/repository/user.repository';
import {
  CreateUserCommand,
  UpdateEmailCommand,
  UpdatePhoneCommand,
} from './dto/user.commands';
import { UserDto } from './dto/user.dto';

/**
 * 用户应用服务
 *
 * 应用层负责：
 *  1. 用例编排（组合领域对象、仓储、领域服务）
 *  2. 事务边界管理
 *  3. DTO ↔ 领域对象的转换
 * 应用层不包含核心业务规则，业务规则都封装在领域模型 User 里。
 */
@Injectable()
export class UserApplicationService {
  private readonly logger = new Logger(UserApplicationService.name);

  constructor(
    @Inject(USER_REPOSITORY)
    private readonly userRepository: UserRepository,
  ) {}

  async createUser(command: CreateUserCommand): Promise<UserDto> {
    this.logger.log(`创建用户: name=${command.name}, email=${command.email}`);

    const existedByName = await this.userRepository.findByName(command.name);
    if (existedByName) {
      throw new ConflictException('用户名已存在');
    }

    const existedByEmail = await this.userRepository.findByEmail(command.email);
    if (existedByEmail) {
      throw new ConflictException('邮箱已被使用');
    }

    const user = User.create({
      name: command.name,
      email: command.email,
      phone: command.phone,
      address: command.address,
    });

    const saved = await this.userRepository.create(user);
    this.logger.log(`用户创建成功: id=${saved.id}`);
    return UserDto.fromEntity(saved);
  }

  async getUserById(id: number): Promise<UserDto> {
    const user = await this.userRepository.findById(id);
    if (!user) {
      throw new NotFoundException(`用户不存在: id=${id}`);
    }
    return UserDto.fromEntity(user);
  }

  async getUserByName(name: string): Promise<UserDto> {
    const user = await this.userRepository.findByName(name);
    if (!user) {
      throw new NotFoundException(`用户不存在: name=${name}`);
    }
    return UserDto.fromEntity(user);
  }

  async getAllUsers(): Promise<UserDto[]> {
    const users = await this.userRepository.findAll();
    return UserDto.fromEntities(users);
  }

  async updateEmail(command: UpdateEmailCommand): Promise<UserDto> {
    const user = await this.userRepository.findById(command.userId);
    if (!user) {
      throw new NotFoundException(`用户不存在: id=${command.userId}`);
    }

    const existed = await this.userRepository.findByEmail(command.email);
    if (existed && existed.id !== command.userId) {
      throw new ConflictException('邮箱已被其他用户使用');
    }

    user.updateEmail(command.email);
    const updated = await this.userRepository.update(user);
    this.logger.log(`用户邮箱更新成功: id=${command.userId}`);
    return UserDto.fromEntity(updated);
  }

  async updatePhone(command: UpdatePhoneCommand): Promise<UserDto> {
    const user = await this.userRepository.findById(command.userId);
    if (!user) {
      throw new NotFoundException(`用户不存在: id=${command.userId}`);
    }

    user.updatePhone(command.phone);
    const updated = await this.userRepository.update(user);
    this.logger.log(`用户手机号更新成功: id=${command.userId}`);
    return UserDto.fromEntity(updated);
  }

  async deleteUser(id: number): Promise<void> {
    const user = await this.userRepository.findById(id);
    if (!user) {
      throw new NotFoundException(`用户不存在: id=${id}`);
    }
    await this.userRepository.delete(id);
    this.logger.log(`用户删除成功: id=${id}`);
  }
}
