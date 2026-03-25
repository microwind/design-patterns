import http from 'node:http'

import { HttpInventoryClient } from '../src/HttpInventoryClient.js'
import { OrderServiceAsync } from '../src/OrderServiceAsync.js'

function assertEqual(expected, actual, message) {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`)
  }
}

;(async function run() {
  let stock = 2

  const server = http.createServer((req, res) => {
    const url = new URL(req.url, 'http://127.0.0.1')
    if (url.pathname !== '/reserve') {
      res.writeHead(404)
      res.end()
      return
    }

    const sku = url.searchParams.get('sku')
    const quantity = Number(url.searchParams.get('quantity') || '0')

    if (sku === 'SKU-BOOK' && quantity > 0 && stock >= quantity) {
      stock -= quantity
      res.writeHead(200)
      res.end('OK')
      return
    }

    res.writeHead(409)
    res.end('NO_STOCK')
  })

  await new Promise((resolve) => server.listen(0, '127.0.0.1', resolve))
  const port = server.address().port

  try {
    const client = new HttpInventoryClient(`http://127.0.0.1:${port}`)
    const service = new OrderServiceAsync(client)

    const success = await service.createOrder('ORD-2001', 'SKU-BOOK', 1)
    assertEqual('CREATED', success.status, 'http status should be CREATED')

    const failed = await service.createOrder('ORD-2002', 'SKU-BOOK', 2)
    assertEqual('REJECTED', failed.status, 'http status should be REJECTED')

    console.log('microservice-basics(js/http) tests passed')
  } finally {
    await new Promise((resolve) => server.close(resolve))
  }
})()
