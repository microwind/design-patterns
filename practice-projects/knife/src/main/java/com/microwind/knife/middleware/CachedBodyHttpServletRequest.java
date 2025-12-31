package com.microwind.knife.middleware;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 可重复读取的 HttpServletRequest 包装类
 * 仅适用于 Spring MVC / Servlet 同步模型
 * <p>
 * 用于缓存请求体内容，使其可以被多次读取
 * <p>
 * 使用场景：
 * - 拦截器需要读取请求体进行签名验证
 * - Controller 也需要读取请求体获取业务参数
 * - InputStream 默认只能读取一次，需要缓存
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存请求体内容
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
        // 如果需要限制大小，可以限制100M
        // if (cachedBody.length > 104857600) {
        //    throw new IOException("Request body too large");
        // }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
    }

    /**
     * 获取缓存的请求体内容
     */
    public String getCachedBody() {
        return new String(this.cachedBody, StandardCharsets.UTF_8);
    }

    /**
     * 缓存的 ServletInputStream 实现
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return cachedBodyInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // 可以不返回 或 throw 更明确的异常说明
//            throw new UnsupportedOperationException();
        }

        @Override
        public int read() {
            return cachedBodyInputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return cachedBodyInputStream.read(b, off, len);
        }

    }
}
