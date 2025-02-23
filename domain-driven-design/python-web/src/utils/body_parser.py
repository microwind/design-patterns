# -*- coding: utf-8 -*-
import json


async def parse_body(request):
    content_length = int(request.headers.get('Content-Length', 0))
    if content_length:
        body = await request.rfile.read(content_length)
        try:
            return json.loads(body.decode('utf-8'))
        except json.JSONDecodeError:
            raise ValueError('无效的 JSON 数据')
    return {}