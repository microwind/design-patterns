# main.py
from flask import Flask
from routes import routes

def create_app():
    app = Flask(__name__)
    # ...其他配置...
    routes.init_app(app)
    return app