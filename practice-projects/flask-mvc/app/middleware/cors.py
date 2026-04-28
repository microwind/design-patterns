from flask_cors import CORS


def cors_middleware(app):
    """Enable CORS for the application"""
    CORS(app, resources={
        r"/api/*": {
            "origins": "*",
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Content-Type", "Authorization", "X-Request-ID"]
        }
    })
