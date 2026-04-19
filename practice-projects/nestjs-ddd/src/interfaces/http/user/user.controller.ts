import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Put,
} from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';
import {
  CreateUserCommand,
  UpdateEmailCommand,
  UpdatePhoneCommand,
} from '../../../application/user/dto/user.commands';
import { UserApplicationService } from '../../../application/user/user-application.service';
import {
  CreateUserRequest,
  UpdateEmailRequest,
  UpdatePhoneRequest,
} from './vo/user.request';
import { UserResponse } from './vo/user.response';

/**
 * 用户 HTTP 接口，路由与 gin-ddd/internal/interfaces/router/user_router.go 对齐：
 *   POST   /api/users
 *   GET    /api/users
 *   GET    /api/users/:id
 *   PUT    /api/users/:id/email
 *   PUT    /api/users/:id/phone
 *   DELETE /api/users/:id
 *
 * 注：/api/users/:id/orders 由订单模块中的 UserOrderController 提供，
 *     这样可以避免用户模块和订单模块之间产生循环依赖。
 */
@ApiTags('用户管理')
@Controller('users')
export class UserController {
  constructor(private readonly userAppService: UserApplicationService) {}

  @Post()
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: '创建用户' })
  async create(@Body() req: CreateUserRequest): Promise<UserResponse> {
    const dto = await this.userAppService.createUser(
      new CreateUserCommand(req.name, req.email, req.phone, req.address),
    );
    return UserResponse.fromDto(dto);
  }

  @Get()
  @ApiOperation({ summary: '获取所有用户' })
  async findAll(): Promise<UserResponse[]> {
    const dtos = await this.userAppService.getAllUsers();
    return UserResponse.fromDtos(dtos);
  }

  @Get(':id')
  @ApiOperation({ summary: '根据 ID 获取用户' })
  async findOne(@Param('id', ParseIntPipe) id: number): Promise<UserResponse> {
    const dto = await this.userAppService.getUserById(id);
    return UserResponse.fromDto(dto);
  }

  @Put(':id/email')
  @ApiOperation({ summary: '更新用户邮箱' })
  async updateEmail(
    @Param('id', ParseIntPipe) id: number,
    @Body() req: UpdateEmailRequest,
  ): Promise<UserResponse> {
    const dto = await this.userAppService.updateEmail(
      new UpdateEmailCommand(id, req.email),
    );
    return UserResponse.fromDto(dto);
  }

  @Put(':id/phone')
  @ApiOperation({ summary: '更新用户手机号' })
  async updatePhone(
    @Param('id', ParseIntPipe) id: number,
    @Body() req: UpdatePhoneRequest,
  ): Promise<UserResponse> {
    const dto = await this.userAppService.updatePhone(
      new UpdatePhoneCommand(id, req.new_phone),
    );
    return UserResponse.fromDto(dto);
  }

  @Delete(':id')
  @ApiOperation({ summary: '删除用户' })
  async remove(@Param('id', ParseIntPipe) id: number): Promise<{ id: number }> {
    await this.userAppService.deleteUser(id);
    return { id };
  }
}
