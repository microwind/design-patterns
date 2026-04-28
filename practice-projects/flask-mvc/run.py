from app import create_app

app = create_app()

if __name__ == '__main__':
    config = app.config.get('CONFIG')
    server_config = config.server if config else {}
    
    app.run(
        host=server_config.get('host', '0.0.0.0'),
        port=server_config.get('port', 8080),
        debug=server_config.get('debug', True)
    )
