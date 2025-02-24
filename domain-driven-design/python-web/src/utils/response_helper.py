# -*- coding: utf-8 -*-
import json

def response_json(response, status_code, data):
    """
    统一返回 JSON 格式的响应
    :param response: HTTPResponse 对象
    :param status_code: 状态码
    :param data: 返回的响应数据，可以是字典、对象或其他类型
    """
    response.send_response(status_code)
    response.send_header('Content-Type', 'application/json')
    response.end_headers()

    # 如果 data 是对象并且具有 to_dict 方法，则调用 to_dict()
    if hasattr(data, 'to_dict'):
        data = data.to_dict()  # 调用对象的 to_dict 方法转换为字典
    elif hasattr(data, '__dict__'):
        data = vars(data)  # 如果没有 to_dict 方法，使用 __dict__ 获取属性

    response.wfile.write(json.dumps(data, ensure_ascii=False).encode('utf-8'))
