import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UserApplicationService } from '../application/user/user-application.service';
import { USER_REPOSITORY } from '../domain/user/repository/user.repository';
import { USER_DATASOURCE } from '../infrastructure/config/typeorm-config.service';
import { UserOrmEntity } from '../infrastructure/persistence/user/user.orm-entity';
import { UserRepositoryImpl } from '../infrastructure/persistence/user/user.repository.impl';
import { UserController } from '../interfaces/http/user/user.controller';

/**
 * 用户模块 - 使用 user_db 数据源（默认 MySQL frog 库）
 *
 * 通过 USER_REPOSITORY 令牌绑定 domain 层的 UserRepository 接口
 * 与 infrastructure 层的 UserRepositoryImpl 实现，完成依赖倒置。
 */
@Module({
  imports: [TypeOrmModule.forFeature([UserOrmEntity], USER_DATASOURCE)],
  controllers: [UserController],
  providers: [
    UserApplicationService,
    {
      provide: USER_REPOSITORY,
      useClass: UserRepositoryImpl,
    },
  ],
  exports: [UserApplicationService, USER_REPOSITORY],
})
export class UserModule {}
