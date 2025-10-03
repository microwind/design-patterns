# utils/response.py
from flask import jsonify

def api_response(data=None, message='success', code=0, status=200):
    return jsonify({
        'code': code,
        'message': message,
        'data': data
    }), status