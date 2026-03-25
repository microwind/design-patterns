from urllib.parse import urlencode
from urllib.request import urlopen
from urllib.error import URLError, HTTPError

from src.inventory_client import InventoryClient


class HttpInventoryClient(InventoryClient):
    def __init__(self, base_url):
        self.base_url = base_url.rstrip('/')

    def reserve(self, sku, quantity):
        query = urlencode({'sku': sku, 'quantity': quantity})
        url = f"{self.base_url}/reserve?{query}"
        try:
            with urlopen(url, timeout=3) as response:
                body = response.read().decode('utf-8')
                return response.status == 200 and body == 'OK'
        except (URLError, HTTPError):
            return False
