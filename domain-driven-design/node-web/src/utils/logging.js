import fs from 'fs';
import path from 'path';

/**
 * 设置日志文件路径
 * @param {string} logFile - 日志文件的路径
 */
let logFilePath = '';  // 声明一个全局变量，用来存储日志文件路径

export function setupLogging(logFile) {
  try {
    // 使用 import.meta.url 获取当前模块的路径，并推导出项目根目录路径
    const currentDir = path.dirname(new URL(import.meta.url).pathname);
    
    // 获取项目根目录路径（假设你是在根目录的某个模块里设置日志路径）
    const rootDir = path.resolve(currentDir, '../..'); // 返回项目根目录

    // 设置日志文件路径为根目录
    logFilePath = path.join(rootDir, logFile); // 现在日志文件路径在根目录下
    
    // 确保日志文件存在，不存在时创建文件
    fs.appendFileSync(logFilePath, '', 'utf8');
    console.log(`日志系统初始化完成，日志写入: ${logFilePath}`);
  } catch (err) {
    console.error(`无法初始化日志文件 ${logFile}: ${err}`);
    process.exit(1);
  }
}

/**
 * 记录请求日志
 * @param {object} req - 请求对象
 * @param {Date} start - 请求开始时间
 */
export function logRequest(req, start) {
  const duration = Date.now() - start;
  const logMessage = `REQUEST: ${req.method} ${req.url} took ${duration}ms\n`;
  logToFile(logMessage);
}

/**
 * 记录普通信息日志
 * @param {string} message - 要记录的信息
 */
export function logInfo(message) {
  const logMessage = `INFO: ${message}\n`;
  logToFile(logMessage);
}

/**
 * 记录错误日志
 * @param {string} message - 错误信息
 */
export function logError(message) {
  const logMessage = `ERROR: ${message}\n`;
  logToFile(logMessage);
}

/**
 * 将日志消息写入文件
 * @param {string} message - 日志消息
 */
function logToFile(message) {
  try {
    fs.appendFileSync(logFilePath, message, 'utf8');
  } catch (err) {
    console.error(`无法写入日志文件 ${logFilePath}: ${err}`);
  }
}
