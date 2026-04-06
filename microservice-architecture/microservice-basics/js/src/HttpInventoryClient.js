import http from 'node:http'

export class HttpInventoryClient {
  constructor(baseUrl) {
    this.baseUrl = new URL(baseUrl)
  }

  reserve(sku, quantity) {
    return new Promise((resolve) => {
      const requestPath = `/reserve?sku=${encodeURIComponent(sku)}&quantity=${quantity}`
      const options = {
        hostname: this.baseUrl.hostname,
        port: this.baseUrl.port,
        path: requestPath,
        method: 'GET',
      }

      const req = http.request(options, (res) => {
        let body = ''
        res.on('data', (chunk) => {
          body += chunk
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
