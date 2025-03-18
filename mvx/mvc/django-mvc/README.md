## Django MVC（MTV）目录结构

```bash
django-order/
├── manage.py                          # 项目管理入口脚本，启动 Django 服务
├── requirements.txt                   # 项目依赖列表
├── config/                            # 全局配置模块
│   ├── __init__.py
│   ├── settings/                      # 分环境的配置（基础、开发、生产等）
│   │   ├── __init__.py
│   │   ├── base.py                  # 公共基础配置
│   │   ├── development.py           # 开发环境配置
│   │   └── production.py            # 生产环境配置
│   ├── urls.py                      # 全局 URL 配置，统一路由入口
│   ├── wsgi.py                      # WSGI 应用，用于部署
│   └── asgi.py                      # ASGI 应用，支持异步
├── apps/                              # 应用（模块）目录
│   └── order/                         # Order 应用（示例模块）
│       ├── __init__.py
│       ├── admin.py                 # Django Admin 后台注册
│       ├── apps.py                  # 应用配置类
│       ├── models.py                # 数据模型定义（订单模型）
│       ├── views.py                 # 视图层（处理请求、执行业务逻辑并返回响应）
│       ├── urls.py                  # 应用内部 URL 路由配置
│       ├── forms.py                 # 表单定义，用于数据验证和处理
│       ├── serializers.py           # 序列化器（如使用 Django REST Framework）
│       ├── tests/                   # 单元测试目录
│       │   ├── __init__.py
│       │   └── test_views.py
│       ├── migrations/              # 数据库迁移记录
│       │   └── __init__.py
│       └── templates/               # 应用模板文件（用于渲染 HTML）
│           └── order/               # Order 应用专用模板
│               └── order_detail.html
├── static/                            # 静态资源（CSS、JS、图片等）
│   └── order/                         # Order 应用专用静态文件
│       ├── css/
│       ├── js/
│       └── images/
└── docs/                              # 项目文档
```


## 目录结构说明
### manage.py
Django 的命令行工具，用于项目管理、数据库迁移、启动开发服务器等任务。

### requirements.txt
列出了项目所需的所有 Python 包，便于环境搭建和依赖管理。

### config/
包含全局配置文件：
    - settings/：分环境的配置，通常将公共基础配置与各环境（开发、生产）特有配置分离。
    - urls.py：全局路由文件，集中管理 URL 分发。
    - wsgi.py/asgi.py：部署时使用的 WSGI/ASGI 应用入口。

### apps/
按功能划分的 Django 应用（模块），每个应用独立实现业务逻辑：
    - order/：订单模块，内部包含模型（models.py）、视图（views.py）、路由（urls.py）、表单（forms.py）、测试（tests/）、模板（templates/）等。
    - 这种分层结构符合 Django 的 MTV 架构理念，其中 View（视图）承担了 Controller 的角色，模板（Template）负责呈现数据，模型（Model）封装数据结构和业务逻辑。

### static/
存放项目的静态资源，如 CSS、JavaScript、图片等，支持前后端分离或模板渲染时的静态资源加载。


## 运行项目
假设已正确配置数据库与环境变量。
```bash
# 安装依赖
$ pip install -r requirements.txt

# 启动开发服务器
$ python manage.py runserver

# 访问接口
$ curl http://localhost:8000/api/orders/1
$ curl -X POST http://localhost:8000/api/orders -d "order_no=20240501"

# 运行测试
$ python manage.py test apps.order
```

## Django 采用 MTV 架构
借鉴了传统 MVC 模型，主要分为以下：
```text
Model（数据模型） → Template（模板层） → View（视图/控制器）
```
- Model：负责定义数据结构和业务逻辑，使用 Django ORM 与数据库交互。
- Template：负责呈现数据，生成 HTML 页面。
- View：负责处理请求、执行业务逻辑并返回响应，相当于传统 MVC 中的 Controller。

### 分层代码

- **路由配置（Router）**
将视图函数与 URL 路径进行绑定，实现请求的分发。
```python
# apps/order/urls.py
from django.urls import path
from . import views

urlpatterns = [
    path('<int:id>/', views.order_detail, name='order_detail'),
    path('create/', views.order_create, name='order_create'),
]
```
在全局路由中引入各应用的路由配置：
```python
# config/urls.py
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('order/', include('apps.order.urls')),  # 将 Order 应用的路由包含进来
]
```

- **视图层（View）视图层**
处理HTTP请求，类似Controller
```python
# apps/order/views.py
from django.http import JsonResponse
from django.views import View
from .services import OrderService
from utils.response import api_response

class OrderAPI(View):
    def get(self, request, order_id):
        order = OrderService.get_order_by_id(order_id)
        return api_response(data=order.to_dict())

    def post(self, request):
        data = request.POST.dict()
        order = OrderService.create_order(data)
        return api_response(data=order.to_dict(), status=201)
```

- **服务层（Service）**
```python
# apps/order/services.py
from .models import Order

class OrderService:
    @staticmethod
    def get_order_by_id(order_id: int) -> Order:
        try:
            return Order.objects.get(id=order_id)
        except Order.DoesNotExist:
            raise OrderNotFoundError(f"Order {order_id} not found")

    @staticmethod
    def create_order(order_data: dict) -> Order:
        return Order.objects.create(**order_data)
```

- **自定义 Manager（类似 Repository 模式）**
```python
# apps/order/managers.py
from django.db import models

class OrderManager(models.Manager):
    def create_order(self, order_no, user, **kwargs):
        """创建订单（带业务校验）"""
        if self.filter(order_no=order_no).exists():
            raise ValueError("订单号已存在")
        return self.create(order_no=order_no, user=user, **kwargs)

    def get_large_orders(self, min_amount=1000):
        """查询大额订单"""
        return self.filter(amount__gte=min_amount).prefetch_related('items')

# 在 Model 中使用
class Order(models.Model):
    objects = OrderManager()  # 替换默认 Manager
    # ... 其他字段 ...
```

- **模型层（Model）**
```python
# apps/order/models.py
from django.db import models

class Order(models.Model):
    order_no = models.CharField(max_length=50, unique=True)
    user_id = models.PositiveIntegerField(db_index=True)
    order_name = models.CharField(max_length=255)
    amount = models.DecimalField(max_digits=10, decimal_places=2)
    status = models.CharField(max_length=50)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'orders'
        ordering = ['-created_at']

    def __str__(self):
        return f'Order {self.order_no}'
```

## 最佳实践
### 中间件配置
```python
# utils/middleware/logging.py
class LoggingMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        print(f"[{request.method}] {request.path}")
        response = self.get_response(request)
        return response

# config/settings.py
MIDDLEWARE = [
    'utils.middleware.logging.LoggingMiddleware',
    # ...
]
```

### 统一响应格式
```python
# utils/response.py
from django.http import JsonResponse

def api_response(data=None, message='success', status=200):
    return JsonResponse({
        'code': 0 if status < 400 else 1,
        'message': message,
        'data': data
    }, status=status)
```

### 接口隔离（Repository模式）

```python
# apps/order/repositories.py（可选）
class OrderRepository:
    @classmethod
    def find_by_status(cls, status: str):
        return Order.objects.filter(status=status)
```

### 数据库迁移
```python
# 生成迁移文件
$ python manage.py makemigrations order

# 执行迁移
$ python manage.py migrate
```

## 最佳实践总结
### 严格分层
    - views.py 仅处理 HTTP 请求解析和响应
    - 业务逻辑集中在 services.py
    - 数据操作通过 Model 的 objects Manager 或自定义 Repository

## 事务管理
- 使用装饰器管理事务
```python
from django.db import transaction

@transaction.atomic
def create_order_with_items(order_data, items_data):
    order = Order.objects.create(**order_data)
    for item in items_data:
        OrderItem.objects.create(order=order, **item)
    return order
```

### 上下文管理器
```python
def update_order_status(order_id, new_status):
    try:
        with transaction.atomic():
            order = Order.objects.select_for_update().get(id=order_id)
            order.status = new_status
            order.save()
            # 其他关联操作...
    except Order.DoesNotExist:
        handle_error()
```

### DRY 原则
    - 复用通用组件（如 api_response）
    - 使用 Django 的 class-based views 减少重复代码

### 安全增强

```python
# 防止 SQL 注入
Order.objects.raw('SELECT * FROM orders WHERE id = %s', [order_id])

# CSRF 保护（默认启用）
@csrf_exempt  # 谨慎使用
class OrderAPI(View): ...
```

### 性能优化

```python
# 使用 select_related/prefetch_related 减少查询
Order.objects.select_related('user').filter(status='pending')

# 添加数据库索引
class Order(models.Model):
    user_id = models.PositiveIntegerField(db_index=True)
```

### 测试覆盖

```python
# apps/order/tests.py
from django.test import TestCase
from .models import Order

class OrderTestCase(TestCase):
    def test_order_creation(self):
        order = Order.objects.create(order_no='TEST123')
        self.assertEqual(order.status, 'pending')
```

## Django 架构优势
### 全栈能力
- 内置 ORM、Admin、模板引擎，适合快速开发全栈应用

### 高扩展性
- 通过中间件、信号机制灵活扩展功能

### 生态丰富
- 可集成 Django REST framework 构建 API
- 支持 Celery 处理异步任务

### 企业级安全
- 默认提供 CSRF、XSS、SQL 注入防护

通过此结构，Django 可实现清晰的 MTV 分层，同时保持开发效率与代码可维护性。

## 总结
1. **高度集成**：内置强大的 ORM、模板系统、认证和管理后台，极大地提高了开发效率。
2. **明确分层**：通过分离 Model、Template 和 View，实现关注点分离，使得代码易于维护和扩展。
3. **丰富生态**：拥有大量第三方库和插件，能够满足各种业务需求，并与其他系统轻松集成。
4. **社区支持强大**：成熟的文档和活跃的社区使得问题解决和技术支持更加便捷。

