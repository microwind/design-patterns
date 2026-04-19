import { Controller, Get } from '@nestjs/common';
import { ApiOperation, ApiTags } from '@nestjs/swagger';

@ApiTags('系统')
@Controller()
export class AppController {
  @Get()
  @ApiOperation({ summary: '欢迎页' })
  welcome() {
    return {
      status: 'ok',
      message: '欢迎使用 NestJS DDD 脚手架！Welcome to NestJS DDD Scaffold.',
    };
  }

  @Get('health')
  @ApiOperation({ summary: '健康检查' })
  health() {
    return {
      status: 'ok',
      message: 'nestjs-ddd service is running',
    };
  }
}
