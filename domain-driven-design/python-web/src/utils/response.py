import json

def response_json(response, status_code, data):
    """
    统一返回 JSON 格式的响应
    :param response: HTTPResponse 对象
    :param status_code: 状态码
    :param data: 返回的响应数据，可以是字典、对象、列表、字符串、整数、浮点数等
    """
    response.send_response(status_code)
    response.send_header('Content-Type', 'application/json')
    response.end_headers()

    # 如果是列表，遍历列表并处理每个元素
    if isinstance(data, list):
        data = [process_item(item) for item in data]
    
    # 如果是字典，直接返回
    elif isinstance(data, dict):
        pass
    
    # 如果是对象，检查是否具有 to_dict 或 __dict__
    elif hasattr(data, 'to_dict'):
        data = data.to_dict()
    elif hasattr(data, '__dict__'):
        data = vars(data)

    # 其他类型（基本数据类型：str, int, float, bool, None）直接使用
    elif isinstance(data, (str, int, float, bool, type(None))):
        pass
    
    # 将处理后的数据写入响应
    response.wfile.write(json.dumps(data, ensure_ascii=False).encode('utf-8'))


def process_item(item):
    """
    处理列表中的每个元素。
    对于对象，尝试调用 to_dict() 或 __dict__，否则保持原样。
    """
    if hasattr(item, 'to_dict'):
        return item.to_dict()
    elif hasattr(item, '__dict__'):
        return vars(item)
    return item
