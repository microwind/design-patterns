## NestJS `MVC`目录结构
```bash
nestjs-order/
├── src/
│   ├── common/                       # 公共模块
│   │   ├── decorators/               # 自定义装饰器
│   │   ├── filters/                  # 异常过滤器
│   │   ├── interceptors/             # 拦截器
│   │   ├── middleware/               # 中间件（如日志、鉴权）
│   │   │   ├── logging.middleware.ts
│   │   │   └── auth.middleware.ts
│   │   └── utils/                    # 通用工具类
│   │       └── response.util.ts
│   │
│   ├── config/                       # 配置模块
│   │   ├── database.config.ts        # 数据库配置
│   │   └── app.config.ts             # 应用配置
│   │
│   ├── order/                        # 订单模块（业务模块）
│   │   ├── dto/                      # 数据传输对象
│   │   │   ├── create-order.dto.ts
│   │   │   └── update-order.dto.ts
│   │   ├── entities/                 # 数据实体（对应Spring models）
│   │   │   └── order.entity.ts
│   │   ├── interfaces/               # 类型接口定义
│   │   ├── order.controller.ts       # 控制器
│   │   ├── order.service.ts          # 服务层
│   │   ├── order.repository.ts       # 数据仓库（TypeORM）
│   │   └── order.module.ts           # 模块定义
│   │
│   ├── app.module.ts                 # 根模块
│   └── main.ts                       # 应用入口
│
├── test/                             # 测试目录
│   ├── e2e/                          # 端到端测试
│   └── order/                        # 模块测试
│       ├── order.controller.spec.ts
│       └── order.service.spec.ts
│
├── public/                           # 静态资源（对应Spring static）
├── views/                            # 模板文件（可选，如hbs模板）
├── .env                              # 环境配置（对应application.properties）
├── package.json                      # 依赖管理
└── tsconfig.json                     # TypeScript配置
```

### 目录结构说明

NestJS 采用模块化架构，每个业务功能（例如 `order` 模块）通常包含以下部分：

- **Controller**  
  处理 HTTP 请求，使用 `@Controller` 装饰器标注。

- **Service**  
  实现业务逻辑，使用 `@Injectable` 装饰器标注。

- **Entities**  
  定义数据实体，使用 TypeORM 的 `@Entity` 装饰器标注。

- **Repository**  
  实现数据访问层，通常配合 TypeORM 使用 `@EntityRepository` 装饰器标注。

- **DTO**  
  定义数据传输对象，用于接口参数校验和数据转换。

- **Module**  
  聚合上述各组件，使用 `@Module` 装饰器标注，构建模块整体。

## 运行项目
```bash
# 安装依赖
$ npm install

# 开发模式
$ npm run start:dev

# 生产打包
$ npm run build
$ npm run start:prod

# 运行测试
$ npm run test
```

## 分层架构
借鉴了传统 MVC 模型，主要分为以下层次：
```text
Controller（接口层） → Service（业务逻辑层） → Repository（数据访问层） → Model（数据模型）
```

### 分层代码
- **控制器层（Controller）**
使用 @Controller 装饰器定义路由和处理 HTTP 请求。
```js
// src/orders/orders.controller.ts
import { Controller, Get, Post, Param, Body, NotFoundException } from '@nestjs/common';
import { OrdersService } from './orders.service';
import { Order } from './order.entity';
import { CreateOrderDto } from './dtos/create-order.dto';

@Controller('api/orders')
export class OrdersController {
  constructor(private readonly ordersService: OrdersService) {}

  @Get(':id')
  async getOrder(@Param('id') id: number): Promise<Order> {
    const order = await this.ordersService.getOrderById(id);
    if (!order) {
      throw new NotFoundException('Order not found');
    }
    return order;
  }

  @Post()
  async createOrder(@Body() createOrderDto: CreateOrderDto): Promise<Order> {
    return this.ordersService.createOrder(createOrderDto);
  }
}
```

- **服务层（Service）**
使用 @Injectable 装饰器定义服务类，处理业务逻辑并调用数据访问层。
```js
// src/order/order.service.ts
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { OrderEntity } from './entities/order.entity';
import { CreateOrderDto } from './dto/create-order.dto';

@Injectable()
export class OrderService {
  constructor(
    @InjectRepository(OrderEntity)
    private readonly orderRepository: Repository<OrderEntity>,
  ) {}

  async findOne(id: string): Promise<OrderEntity> {
    return this.orderRepository.findOneBy({ id });
  }

  async create(createOrderDto: CreateOrderDto): Promise<OrderEntity> {
    const order = this.orderRepository.create({
      ...createOrderDto,
      createdAt: new Date(),
    });
    return this.orderRepository.save(order);
  }
}
```

- **数据仓库（Repository）**
Repository 层是数据访问的核心抽象层，负责与数据库交互
```js
// src/order/order.repository.ts
import { Injectable } from '@nestjs/common';
import { DataSource, Repository } from 'typeorm';
import { OrderEntity } from './entities/order.entity';

@Injectable()
export class OrderRepository extends Repository<OrderEntity> {
  constructor(private dataSource: DataSource) {
    super(OrderEntity, dataSource.createEntityManager());
  }

  // 自定义查询方法示例
  async findByStatus(status: string): Promise<OrderEntity[]> {
    return this.createQueryBuilder('order')
      .where('order.status = :status', { status })
      .getMany();
  }

  // 原生 SQL 查询示例
  async findLargeOrders(threshold: number): Promise<OrderEntity[]> {
    return this.query(
      'SELECT * FROM orders WHERE amount > $1',
      [threshold]
    );
  }
}
```

- **数据实体（Entity）**
实体定义，与数据库表结构对应。
```js
// src/order/entities/order.entity.ts
import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity('orders')
export class OrderEntity {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ unique: true })
  orderNo: string;

  @Column()
  userId: string;

  @Column()
  orderName: string;

  @Column('decimal', { precision: 10, scale: 2 })
  amount: number;

  @Column()
  status: string;

  @Column({ type: 'timestamp', default: () => 'CURRENT_TIMESTAMP' })
  createdAt: Date;
}
```

### 模块定义（Module）
模块定义，组织上述组件并提供模块的配置。
```js
// src/order/order.module.ts
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { OrderController } from './order.controller';
import { OrderService } from './order.service';
import { OrderEntity } from './entities/order.entity';

@Module({
  imports: [TypeOrmModule.forFeature([OrderEntity])],
  controllers: [OrderController],
  providers: [OrderService],
})
export class OrderModule {}
```

### 核心机制
**依赖注入**
通过构造函数自动注入服务
```typescript
constructor(private readonly orderService: OrderService) {}
```

**模块化组织**
每个功能模块独立封装，通过 @Module 声明依赖：

```typescript
@Module({
  imports: [/* 其他模块 */],
  controllers: [/* 控制器 */],
  providers: [/* 服务/仓库 */],
  exports: [/* 暴露的服务 */]
})
```

**数据验证**
使用 class-validator + DTO 进行参数校验：

```typescript
// create-order.dto.ts
import { IsString, IsDecimal } from 'class-validator';

export class CreateOrderDto {
  @IsString()
  orderNo: string;

  @IsDecimal()
  amount: number;
}
```

**全局配置**
通过 ConfigModule 加载环境变量：

```typescript
// app.module.ts
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    TypeOrmModule.forRootAsync({ useClass: DatabaseConfig }),
  ]
})
```

### 数据库配置（TypeORM）

```ts
// src/config/database.config.ts
import { TypeOrmModuleOptions } from '@nestjs/typeorm';

export default (): TypeOrmModuleOptions => ({
  type: 'mysql',
  host: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT),
  username: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  entities: [__dirname + '/../**/*.entity{.ts,.js}'],
  synchronize: process.env.NODE_ENV !== 'production',
});
```

### 环境文件（.env）
```bash
# .env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=secret
DB_NAME=order_db
```

## 高级特性
### 拦截器（Interceptors）
```ts
// src/common/interceptors/logging.interceptor.ts
import { Injectable, NestInterceptor, ExecutionContext, CallHandler } from '@nestjs/common';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable()
export class LoggingInterceptor implements NestInterceptor {
  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    console.log('Before request...');
    const now = Date.now();
    return next.handle().pipe(
      tap(() => console.log(`Request completed in ${Date.now() - now}ms`)),
    );
  }
}
```

### 全局异常过滤
```ts
// src/common/filters/http-exception.filter.ts
import { ExceptionFilter, Catch, ArgumentsHost } from '@nestjs/common';
import { Response } from 'express';

@Catch()
export class HttpExceptionFilter implements ExceptionFilter {
  catch(exception: Error, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    
    response.status(500).json({
      statusCode: 500,
      message: exception.message
    });
  }
}
```

### 中间件（Middleware）
```ts
// src/common/middleware/logging.middleware.ts
import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';

@Injectable()
export class LoggingMiddleware implements NestMiddleware {
  use(req: Request, res: Response, next: NextFunction) {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.path}`);
    next();
  }
}
```

## 最佳实践

**严格分层**
Controller 仅处理 HTTP 相关逻辑，业务逻辑集中在 Service 层

**使用DTO验证**
所有接口入参使用 class-validator 进行校验

**异步处理**
使用 async/await 或 Observable 处理异步操作

**依赖注入**
避免直接实例化类，通过构造函数注入依赖

**环境隔离**
使用 ConfigModule 区分开发/生产环境配置

通过此结构，NestJS 可实现经典MVC 分层，同时保持 JS/TypeScript 语言的简洁动态特性。

## 总结
1. **模块化与依赖注入**：利用模块和依赖注入将控制器、服务、数据访问层、DTO 等划分清晰，确保各司其职，便于维护和单元测试。
2. **装饰器与 TypeScript**：通过装饰器简化路由映射、验证、管道、异常处理等配置，充分利用 TypeScript 的类型检查和自动补全，提高开发效率。
3. **灵活扩展**：模块化设计便于功能扩展和集成第三方库（如 TypeORM、GraphQL），同时支持微服务和事件驱动架构，适应多种业务场景。
4. **开箱即用**：NestJS 提供了丰富的 CLI 工具、内置中间件和最佳实践，帮助开发者快速构建和部署高质量的后端服务。

