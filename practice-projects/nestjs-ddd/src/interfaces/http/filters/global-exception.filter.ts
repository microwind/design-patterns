import {
  ArgumentsHost,
  Catch,
  ExceptionFilter,
  HttpException,
  HttpStatus,
  Logger,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { ApiResponse } from '../common/api-response';

/**
 * 全局异常过滤器：
 * - HttpException 按其状态码返回；
 * - 业务领域抛出的 Error（如聚合根校验错误）统一为 400；
 * - 其他未知错误为 500。
 */
@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {
  private readonly logger = new Logger(GlobalExceptionFilter.name);

  catch(exception: unknown, host: ArgumentsHost): void {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();

    let status = HttpStatus.INTERNAL_SERVER_ERROR;
    let code = 5000;
    let message = '服务器内部错误';

    if (exception instanceof HttpException) {
      status = exception.getStatus();
      code = status;
      const resp = exception.getResponse();
      if (typeof resp === 'string') {
        message = resp;
      } else if (typeof resp === 'object' && resp !== null) {
        const r = resp as { message?: string | string[] };
        if (Array.isArray(r.message)) {
          message = r.message.join('; ');
        } else if (typeof r.message === 'string') {
          message = r.message;
        } else {
          message = exception.message;
        }
      }
    } else if (exception instanceof Error) {
      status = HttpStatus.BAD_REQUEST;
      code = 4000;
      message = exception.message;
    }

    this.logger.error(
      `[${request.method}] ${request.url} -> ${status} ${message}`,
      exception instanceof Error ? exception.stack : undefined,
    );

    response.status(status).json(ApiResponse.fail(code, message));
  }
}
