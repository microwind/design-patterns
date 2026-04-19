import {
  CallHandler,
  ExecutionContext,
  Injectable,
  NestInterceptor,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiResponse } from '../common/api-response';

/**
 * 全局响应拦截器：所有 Controller 返回值统一包装成 ApiResponse。
 * 如果 Controller 已经显式返回 ApiResponse，则保持原样。
 */
@Injectable()
export class TransformResponseInterceptor<T>
  implements NestInterceptor<T, ApiResponse<T>>
{
  intercept(
    _context: ExecutionContext,
    next: CallHandler<T>,
  ): Observable<ApiResponse<T>> {
    return next.handle().pipe(
      map((data) => {
        if (data instanceof ApiResponse) {
          return data as ApiResponse<T>;
        }
        return ApiResponse.success<T>(data);
      }),
    );
  }
}
