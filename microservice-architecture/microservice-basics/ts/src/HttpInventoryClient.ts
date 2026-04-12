/**
 * @file HttpInventoryClient.ts - HTTP 远程库存客户端（阶段2）
 *
 * 【设计模式】
 *   - 适配器模式（Adapter Pattern）：将 HTTP 远程调用适配为 AsyncInventoryClient 接口。
 *   - 代理模式（Proxy Pattern）：作为远程库存服务的本地代理。
 *
 * 【架构思想】
 *   当库存服务独立部署后，订单服务通过 HTTP 客户端调用远程接口。
 *   返回 Promise 体现了远程调用的异步本质。
 *
 * 【开源对比】
 *   - axios / node-fetch：常用 HTTP 客户端
 *   - tRPC：端到端类型安全的 RPC 调用
 *   本示例使用 Node.js 原生 http 模块。
 */

import http from 'http'

/** HTTP 远程库存客户端 */
export class HttpInventoryClient {
  /**
   * @param baseUrl 库存服务的基础 URL
   */
  constructor(private readonly baseUrl: URL) {}

  /**
   * 通过 HTTP GET 调用远程库存服务进行库存预留。
   * @returns Promise<boolean> true=预留成功，false=失败或网络异常
   */
  reserve(sku: string, quantity: number): Promise<boolean> {
    return new Promise((resolve) => {
      // 构建请求路径，对 SKU 进行 URL 编码
      const path = `/reserve?sku=${encodeURIComponent(sku)}&quantity=${quantity}`
      const options = {
        hostname: this.baseUrl.hostname,
        port: this.baseUrl.port,
        path,
        method: 'GET',
      }

      // 发送 HTTP 请求
      const req = http.request(options, (res: any) => {
        let body = ''
        res.on('data', (chunk: any) => {
          body += chunk.toString('utf-8')
        })
        res.on('end', () => {
          // 判断预留是否成功：HTTP 200 且响应体为 "OK"
          resolve(res.statusCode === 200 && body === 'OK')
        })
      })

      // 网络异常时返回 false
      req.on('error', () => resolve(false))
      req.end()
    })
  }
}
