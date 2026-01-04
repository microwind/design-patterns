package com.github.microwind.springboot4ddd.infrastructure.middleware;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 可重复读取的 HttpServletRequest 包装类
 * <p>
 * 用于缓存请求体内容，使其可以被多次读取
 *
 * @author jarry
 * @since 1.0.0
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // 缓存请求体内容
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
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
            throw new UnsupportedOperationException("Not supported in sync servlet mode");
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
