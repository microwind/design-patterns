// logging_middleware.js
function loggingMiddleware(req, res, next) {
  const start = Date.now();
  res.on('finish', () => {
    const duration = Date.now() - start;
    console.log(`REQUEST: ${req.method} ${req.url} took ${duration}ms`);
  });
  next();
}

module.exports = { loggingMiddleware };