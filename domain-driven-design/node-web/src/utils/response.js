// src/utils/response.js

function sendResponse(res, statusCode, data, contentType = 'application/json', headers = {}) {
  res.writeHead(statusCode, {
    'Content-Type': contentType,
    ...headers, // 支持自定义头部
  });

  // 根据不同的 contentType 处理响应内容
  if (contentType === 'application/json') {
    res.end(JSON.stringify(data));
  } else if (contentType === 'text/plain') {
    res.end(data);
  } else if (contentType === 'application/xml') {
    res.end(data); // 可以在这里加入XML格式化逻辑
  } else if (contentType === 'text/html') {
    res.end(data); // 可以返回HTML内容
  } else if (contentType === 'application/octet-stream') {
    res.end(data); // 用于二进制数据，比如文件下载
  } else {
    res.end(data);
  }
}

function sendError(res, statusCode, message, contentType = 'application/json', headers = {}) {
  res.writeHead(statusCode, {
    'Content-Type': contentType,
    ...headers,
  });

  const errorResponse = contentType === 'application/json' ?
    {
      error: message
    } :
    message;

  if (contentType === 'application/json') {
    res.end(JSON.stringify(errorResponse));
  } else {
    res.end(errorResponse);
  }
}

function sendNoContent(res) {
  res.writeHead(204, {
    'Content-Length': '0'
  });
  res.end();
}

function sendFile(res, filePath, fileName, contentType = 'application/octet-stream') {
  const fs = require('fs');
  const path = require('path');
  const fileStream = fs.createReadStream(filePath);

  res.writeHead(200, {
    'Content-Type': contentType,
    'Content-Disposition': `attachment; filename="${fileName}"`,
    'Content-Length': fs.statSync(filePath).size,
  });

  fileStream.pipe(res);
}

function setCacheHeaders(res, cacheDuration = 3600) {
  const expirationDate = new Date();
  expirationDate.setSeconds(expirationDate.getSeconds() + cacheDuration);

  res.setHeader('Cache-Control', `public, max-age=${cacheDuration}`);
  res.setHeader('Expires', expirationDate.toUTCString());
}

function setCorsHeaders(res, origin = '*') {
  res.setHeader('Access-Control-Allow-Origin', origin);
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
}

export {
  sendResponse,
  sendError,
  sendNoContent,
  sendFile,
  setCacheHeaders,
  setCorsHeaders
};