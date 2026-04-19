# NestJS DDD 脚手架 · 开发指南

> 本文是 [`NestJS-DDD-Scaffold.md`](https://github.com/microwind/design-patterns/blob/main/practice-projects/nestjs-ddd/NestJS-DDD-Scaffold.md) 的配套开发手册，面向**已经把项目跑起来的开发者**，讲清楚：接口一览、如何新增业务模块、统一响应与异常约定、开发规范、常见问题排查。

## 目录

- [1. API 接口一览](#1-api-接口一览)
- [2. 统一响应与异常处理](#2-统一响应与异常处理)
- [3. 如何新增一个业务模块（以 Product 为例）](#3-如何新增一个业务模块以-product-为例)
- [4. 开发规范](#4-开发规范)
- [5. 单元测试建议](#5-单元测试建议)
- [6. 常见问题排查](#6-常见问题排查)
- [7. 常用命令](#7-常用命令)

## 1. API 接口一览

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | POST | `/api/users` | 创建用户 |
| 用户 | GET | `/api/users/:id` | 查询用户 |
| 用户 | GET | `/api/users?name=xxx&email=xxx` | 条件查询 |
| 用户 | PUT | `/api/users/:id/phone` | 更新手机号 |
| 用户 | PUT | `/api/users/:id/address` | 更新地址 |
| 用户 | DELETE | `/api/users/:id` | 删除用户 |
| 订单 | POST | `/api/orders` | 创建订单 |
| 订单 | GET | `/api/orders/:id` | 查询订单 |
| 订单 | GET | `/api/users/:id/orders` | 查询某用户的订单 |
| 订单 | PUT | `/api/orders/:id/pay` | 支付 |
| 订单 | PUT | `/api/orders/:id/ship` | 发货 |
| 订单 | PUT | `/api/orders/:id/deliver` | 确认送达 |
| 订单 | PUT | `/api/orders/:id/cancel` | 取消 |
| 订单 | PUT | `/api/orders/:id/refund` | 退款 |
| 系统 | GET | `/health` | 健康检查 |

> 完整参数、示例、错误码见 Swagger：`http://localhost:8080/docs`。请求体与响应体统一采用 **snake_case** 字段命名。

## 2. 统一响应与异常处理

### 成功响应

Controller 只需返回 DTO / VO，由 `TransformResponseInterceptor` 自动包装：

```json
{ "code": 200, "message": "success", "data": { "id": 1, "name": "jarry" } }
```

### 异常响应

业务里直接 `throw new NotFoundException(...)` 或 `throw new Error(...)`，由 `GlobalExceptionFilter` 统一拦截：

```json
{ "code": 404, "message": "用户不存在: id=99", "data": null }
```

### 常用异常对照

| 场景 | 抛出 | 返回 code |
|------|------|-----------|
| 资源不存在 | `NotFoundException` | 404 |
| 参数非法 / 校验失败 | `BadRequestException` / ValidationPipe | 400 |
| 业务规则冲突（如状态非法） | `ConflictException` 或 `throw new Error('...')` | 409 / 500 |
| 未授权 | `UnauthorizedException` | 401 |

## 3. 如何新增一个业务模块（以 Product 为例）

以"商品（Product）"为例，完整走一遍 DDD 七步法。

### 第 1 步：写领域模型（纯 TypeScript）

`src/domain/product/model/product.entity.ts`

```ts
export class Product {
  constructor(
    public readonly id: number,
    public name: string,
    public price: number,
    public stock: number,
  ) {}

  reduceStock(qty: number): void {
    if (qty <= 0) throw new Error('扣减数量必须大于 0');
    if (this.stock < qty) throw new Error('库存不足');
    this.stock -= qty;
  }
}
```

### 第 2 步：定义仓储接口

`src/domain/product/repository/product.repository.ts`

```ts
export const PRODUCT_REPOSITORY = Symbol('PRODUCT_REPOSITORY');

export interface ProductRepository {
  findById(id: number): Promise<Product | null>;
  save(product: Product): Promise<Product>;
}
```

### 第 3 步：实现基础设施层

`src/infrastructure/persistence/product/product.orm-entity.ts`：TypeORM 实体。

`src/infrastructure/persistence/product/product.repository.impl.ts`：

```ts
@Injectable()
export class ProductRepositoryImpl implements ProductRepository {
  constructor(
    @InjectRepository(ProductOrmEntity, USER_DATASOURCE)
    private readonly repo: Repository<ProductOrmEntity>,
  ) {}

  async findById(id: number): Promise<Product | null> {
    const po = await this.repo.findOneBy({ id });
    return po ? new Product(po.id, po.name, po.price, po.stock) : null;
  }

  async save(product: Product): Promise<Product> { /* ... */ }
}
```

### 第 4 步：应用服务编排用例

`src/application/product/product-application.service.ts`

```ts
@Injectable()
export class ProductApplicationService {
  constructor(
    @Inject(PRODUCT_REPOSITORY)
    private readonly repo: ProductRepository,
  ) {}

  async reduceStock(id: number, qty: number): Promise<void> {
    const product = await this.repo.findById(id);
    if (!product) throw new NotFoundException(`商品不存在: id=${id}`);
    product.reduceStock(qty);
    await this.repo.save(product);
  }
}
```

### 第 5 步：接口层（VO + Controller）

`src/interfaces/http/product/vo/reduce-stock.request.ts`：

```ts
export class ReduceStockRequest {
  @ApiProperty({ example: 2 })
  @IsInt() @Min(1)
  qty!: number;
}
```

`src/interfaces/http/product/product.controller.ts`：

```ts
@Controller('products')
export class ProductController {
  constructor(private readonly svc: ProductApplicationService) {}

  @Put(':id/reduce-stock')
  async reduce(@Param('id') id: number, @Body() req: ReduceStockRequest) {
    await this.svc.reduceStock(Number(id), req.qty);
    return { id, qty: req.qty };
  }
}
```

### 第 6 步：模块装配

`src/modules/product.module.ts`：

```ts
@Module({
  imports: [TypeOrmModule.forFeature([ProductOrmEntity], USER_DATASOURCE)],
  controllers: [ProductController],
  providers: [
    ProductApplicationService,
    { provide: PRODUCT_REPOSITORY, useClass: ProductRepositoryImpl },
  ],
})
export class ProductModule {}
```

然后把 `ProductModule` 加到 `app.module.ts` 的 `imports` 中。

### 第 7 步：建表

```sql
-- docs/init.mysql.sql 追加
DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  stock INT NOT NULL DEFAULT 0
);
```

整个流程 **不用动原有模块的任何代码**，符合"对扩展开放、对修改关闭"的 OCP 原则。

## 4. 开发规范

### 4.1 命名约定

| 类型 | 命名 | 示例 |
|------|------|------|
| 聚合根 / 实体 | 业务名词 | `User` / `Order` |
| 仓储接口 | `XxxRepository` | `UserRepository` |
| 仓储实现 | `XxxRepositoryImpl` | `UserRepositoryImpl` |
| ORM 实体 | `XxxOrmEntity` | `OrderOrmEntity` |
| 应用服务 | `XxxApplicationService` | `OrderApplicationService` |
| Command | `XxxCommand` | `CreateOrderCommand` |
| DTO | `XxxDto` | `OrderDto` |
| HTTP 请求 | `XxxRequest` | `CreateOrderRequest` |
| HTTP 响应 | `XxxResponse` | `OrderResponse` |
| 领域事件 | `XxxEvent` | `OrderCreatedEvent` |

### 4.2 分层原则（强约束）

- **领域层** 不能 `import` 任何 `@nestjs/*` / `typeorm` / `class-validator`，必须保持纯 TypeScript。
- **应用层** 只依赖领域层接口（仓储、事件发布），不直接使用 TypeORM。
- **基础设施层** 实现领域层定义的接口，通过 NestJS DI 注入。
- **接口层** 只做参数校验和协议转换，业务规则必须推到领域层。

### 4.3 提交与代码风格

- 统一使用 Prettier 格式化：`npm run format`
- ESLint 检查：`npm run lint`
- 关键路径请附上 JSDoc / 类型注释，避免无脑 `any`

## 5. 单元测试建议

| 层 | 测试重点 | 工具 | 建议覆盖率 |
|----|---------|------|----------|
| 领域层 | 聚合根业务规则（状态机、不变量） | Jest 纯单测 | > 90% |
| 应用层 | 用例编排、事务、异常路径 | Jest + Mock 仓储 | > 80% |
| 接口层 | 请求校验、HTTP 协议 | `@nestjs/testing` + `supertest` | > 60% |
| 基础设施层 | 仓储 SQL、事件投递 | 集成测试（testcontainers） | 视情况 |

领域层的聚合根（如 `Order`）**完全不依赖任何框架**，可以毫无负担地写大量纯单测，这是 DDD 最大的红利之一。

## 6. 常见问题排查

| 现象 | 根因 | 解决 |
|------|------|------|
| `column "order_no" of relation "orders" contains null values` | 旧表结构残留 + synchronize 冲突 | 重跑 `docs/init.postgres.sql` |
| `Duplicate entry '' for key 'users.IDX_xxx'` | users 表脏数据 | 重跑 `docs/init.mysql.sql` |
| `ECONNREFUSED 127.0.0.1:3306/5432` | 数据库未启动 | 启动本地 DB 或 `docker compose up -d mysql postgres` |
| `Access denied for user 'frog_admin'` | 账号密码与 `config.yaml` 不一致 | 按 yaml 创建账号或调整 yaml |
| Swagger 页面打不开 | 启动失败 / 端口冲突 | 看终端日志，调 `config.yaml: server.port` |
| `Cannot find module '@nestjs/...'` | 依赖未装 | `npm install` |
| `Nest can't resolve dependencies of XxxService` | 忘记在 Module 里注册 provider | 检查对应 `modules/*.module.ts` 的 `providers` |
| 响应字段出现 camelCase | Response VO 没写 `snake_case` 属性名 | 参照现有 VO，显式声明 snake_case 字段 |

## 7. 常用命令

```bash
npm run start:dev        # 开发模式（热重载）
npm run build            # 编译到 dist/
npm run start:prod       # 生产启动
npm run lint             # ESLint 自动修复
npm run format           # Prettier 格式化
npm run test             # Jest 单测
npm run test:cov         # 覆盖率报告

docker compose up -d     # 拉起 MySQL + PostgreSQL + App
docker compose logs -f app
docker compose down -v   # 彻底清理（含数据卷）
```

## 延伸阅读

- 主文：[`一款开箱即用的NestJS-DDD脚手架.md`](https://github.com/microwind/design-patterns/tree/main/practice-projects/nestjs-ddd/NestJS-DDD-Scaffold.md)
- DDD 原理与多语言对比：[`domain-driven-design/`](https://github.com/microwind/design-patterns/tree/main/domain-driven-design)
- Go 同款脚手架：[`gin-ddd/Gin-Framework-DDD-Scaffold.md`](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd)
- Java 同款脚手架：[`springboot4ddd/Springboot4DDD-Scaffold.md`](https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd)
