import { Logger, ValidationPipe } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { NestFactory } from '@nestjs/core';
import { NestExpressApplication } from '@nestjs/platform-express';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import { GlobalExceptionFilter } from './interfaces/http/filters/global-exception.filter';
import { TransformResponseInterceptor } from './interfaces/http/interceptors/transform-response.interceptor';

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(AppModule, {
    logger: ['log', 'error', 'warn', 'debug', 'verbose'],
  });

  const config = app.get(ConfigService);
  const host = config.get<string>('server.host', '0.0.0.0');
  const port = config.get<number>('server.port', 8080);
  const apiPrefix = config.get<string>('server.apiPrefix', 'api');

  app.setGlobalPrefix(apiPrefix, { exclude: ['', 'health'] });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: false,
      transform: true,
      transformOptions: { enableImplicitConversion: true, excludeExtraneousValues: false },
    }),
  );
  app.useGlobalInterceptors(new TransformResponseInterceptor());
  app.useGlobalFilters(new GlobalExceptionFilter());

  app.enableCors();

  const swaggerConfig = new DocumentBuilder()
    .setTitle('NestJS DDD Scaffold')
    .setDescription('基于 NestJS 的领域驱动设计脚手架 API（与 gin-ddd 对齐）')
    .setVersion('1.0.0')
    .addTag('用户管理')
    .addTag('订单管理')
    .addTag('系统')
    .build();
  const document = SwaggerModule.createDocument(app, swaggerConfig);
  SwaggerModule.setup('docs', app, document);

  await app.listen(port, host);

  const logger = new Logger('Bootstrap');
  logger.log('========================================');
  logger.log(`NestJS DDD 服务已启动: http://${host}:${port}`);
  logger.log(`API 前缀: /${apiPrefix}`);
  logger.log(`Swagger 文档: http://${host}:${port}/docs`);
  logger.log('========================================');
}

bootstrap();
