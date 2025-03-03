// src/middleware/logging-middleware.js
import { logRequest } from '../utils/logging.js';
export default function loggingMiddleware(req, res, next) {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    console.log(`REQUEST: ${req.method} ${req.url} took ${duration}ms`);
    // 使用 logging utils 记录到文件
    logRequest(req, start);
  });
  next();
}