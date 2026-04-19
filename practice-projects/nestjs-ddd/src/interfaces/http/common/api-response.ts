/**
 * 统一 API 响应结构，与 gin-ddd 的 common.Response 对齐：
 *   { code, message, data }
 * 成功时 code=0，错误时 code 为业务码（HTTP 状态码 or 自定义）。
 */
export class ApiResponse<T = unknown> {
  code!: number;
  message!: string;
  data?: T;

  static success<T>(data?: T, message = 'success'): ApiResponse<T> {
    const res = new ApiResponse<T>();
    res.code = 0;
    res.message = message;
    res.data = data;
    return res;
  }

  static fail(code: number, message: string): ApiResponse<null> {
    const res = new ApiResponse<null>();
    res.code = code;
    res.message = message;
    return res;
  }
}
