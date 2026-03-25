import http from 'http'
import type { IncomingMessage } from 'http'

export class HttpInventoryClient {
  constructor(private readonly baseUrl: URL) {}

  reserve(sku: string, quantity: number): Promise<boolean> {
    return new Promise((resolve) => {
      const path = `/reserve?sku=${encodeURIComponent(sku)}&quantity=${quantity}`
      const options: http.RequestOptions = {
        hostname: this.baseUrl.hostname,
        port: this.baseUrl.port,
        path,
        method: 'GET',
      }

      const req = http.request(options, (res: IncomingMessage) => {
        let body = ''
        res.on('data', (chunk: Buffer) => {
          body += chunk.toString('utf-8')
        })
        res.on('end', () => {
          resolve(res.statusCode === 200 && body === 'OK')
        })
      })

      req.on('error', () => resolve(false))
      req.end()
    })
  }
}
