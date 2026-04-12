"""
http_inventory_client.py - HTTP 远程库存客户端（阶段2）

【设计模式】
  - 适配器模式（Adapter Pattern）：将 HTTP 远程调用适配为 InventoryClient 接口，
    调用方（OrderService）无需感知底层是 HTTP 通信。
  - 代理模式（Proxy Pattern）：作为远程库存服务的本地代理。

【架构思想】
  当库存服务独立部署后，订单服务通过 HTTP 客户端调用远程接口。
  "远程调用不能当成本地函数调用"——需要处理网络超时、连接失败等问题。

【开源对比】
  - requests / httpx：Python 常用 HTTP 客户端库
  - gRPC-Python：高性能 RPC 客户端
  本示例使用 Python 标准库 urllib，展示最基础的 HTTP 通信。
"""

from urllib.parse import urlencode
from urllib.request import urlopen
from urllib.error import URLError, HTTPError

from src.inventory_client import InventoryClient


class HttpInventoryClient(InventoryClient):
    """HTTP 远程库存客户端，将 HTTP 调用适配为 InventoryClient 接口。

    Attributes:
        base_url: 库存服务的基础 URL（如 http://localhost:8080）
    """

    def __init__(self, base_url):
        """构造 HTTP 库存客户端。

        Args:
            base_url: 库存服务的基础 URL
        """
        self.base_url = base_url.rstrip('/')

    def reserve(self, sku, quantity):
        """通过 HTTP 远程调用库存服务进行库存预留。

        Args:
            sku: 商品 SKU 编码
            quantity: 预留数量

        Returns:
            bool: True=预留成功（HTTP 200 + 响应体 "OK"），False=失败或网络异常
        """
        # URL 编码参数，防止特殊字符导致请求异常
        query = urlencode({'sku': sku, 'quantity': quantity})
        url = f"{self.base_url}/reserve?{query}"
        try:
            # 发送 HTTP 请求，设置 3 秒超时
            with urlopen(url, timeout=3) as response:
                body = response.read().decode('utf-8')
                # 判断预留是否成功
                return response.status == 200 and body == 'OK'
        except (URLError, HTTPError):
            # 网络异常（连接失败、超时等），返回失败
            return False
