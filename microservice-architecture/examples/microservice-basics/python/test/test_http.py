import os
import sys
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import urlparse, parse_qs

sys.path.append(os.getcwd())

from src.http_inventory_client import HttpInventoryClient
from src.order_service import OrderService


class InventoryHandler(BaseHTTPRequestHandler):
    stock = 2

    def do_GET(self):
        parsed = urlparse(self.path)
        if parsed.path != '/reserve':
            self.send_response(404)
            self.end_headers()
            return

        qs = parse_qs(parsed.query)
        sku = qs.get('sku', [''])[0]
        quantity = int(qs.get('quantity', ['0'])[0])

        if sku == 'SKU-BOOK' and quantity > 0 and InventoryHandler.stock >= quantity:
            InventoryHandler.stock -= quantity
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b'OK')
            return

        self.send_response(409)
        self.end_headers()
        self.wfile.write(b'NO_STOCK')

    def log_message(self, fmt, *args):
        return


def assert_equal(expected, actual, message):
    if expected != actual:
        raise RuntimeError(f"{message} expected={expected} actual={actual}")


def main():
    server = ThreadingHTTPServer(('127.0.0.1', 0), InventoryHandler)
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()

    base_url = f"http://127.0.0.1:{server.server_port}"

    try:
        service = OrderService(HttpInventoryClient(base_url))

        success = service.create_order('ORD-2001', 'SKU-BOOK', 1)
        assert_equal('CREATED', success.status, 'http status should be CREATED')

        failed = service.create_order('ORD-2002', 'SKU-BOOK', 2)
        assert_equal('REJECTED', failed.status, 'http status should be REJECTED')

        print('microservice-basics(python/http) tests passed')
    finally:
        server.shutdown()
        server.server_close()


if __name__ == '__main__':
    main()
