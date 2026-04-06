import http from 'http'

import { HttpInventoryClient } from '../src/HttpInventoryClient.js'
import { OrderServiceAsync } from '../src/OrderServiceAsync.js'

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${String(expected)} actual=${String(actual)}`)
  }
}

;(async function run(): Promise<void> {
  let stock = 2

  const server = http.createServer((req: any, res: any) => {
    const url = new URL(req.url ?? '/', 'http://127.0.0.1')

    if (url.pathname !== '/reserve') {
      res.writeHead(404)
      res.end()
      return
    }

    const sku = url.searchParams.get('sku') ?? ''
    const quantity = Number(url.searchParams.get('quantity') ?? '0')

    if (sku === 'SKU-BOOK' && quantity > 0 && stock >= quantity) {
      stock -= quantity
      res.writeHead(200)
      res.end('OK')
      return
    }

    res.writeHead(409)
    res.end('NO_STOCK')
  })

  await new Promise<void>((resolve) => server.listen(0, '127.0.0.1', resolve))
  const address = server.address() as { port: number } | string | null
  if (!address || typeof address === 'string') {
    throw new Error('invalid server address')
  }

  try {
    const client = new HttpInventoryClient(new URL(`http://127.0.0.1:${address.port}`))
    const service = new OrderServiceAsync(client)

    const success = await service.createOrder('ORD-2001', 'SKU-BOOK', 1)
    assertEqual('CREATED', success.status, 'http status should be CREATED')

    const failed = await service.createOrder('ORD-2002', 'SKU-BOOK', 2)
    assertEqual('REJECTED', failed.status, 'http status should be REJECTED')

    console.log('microservice-basics(ts/http) tests passed')
  } finally {
    await new Promise<void>((resolve) => server.close(() => resolve()))
  }
})()
