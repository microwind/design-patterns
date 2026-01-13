package com.github.microwind.springboot4ddd.infrastructure.middleware;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 可重复读取的 HttpServletRequest 包装类
 * <p>
 * 用于缓存请求体内容,使其可以被多次读取
 *
 * @author jarry
 * @since 1.0.0
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private final byte[] cachedBody;
    private final Charset charset;

    public CachedBodyHttpServletRequest(HttpServletRequest request, int maxCacheSize) throws IOException {
        super(request);

        // 获取请求字符编码
        String encoding = request.getCharacterEncoding();
        this.charset = (encoding != null) ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        // 缓存请求体内容
        InputStream inputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(inputStream);

        // 验证实际大小
        if (this.cachedBody.length > maxCacheSize) {
            throw new IllegalArgumentException(
                    String.format("请求体实际大小 %d bytes 超过限制 %d bytes", this.cachedBody.length, maxCacheSize)
            );
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(this.cachedBody), this.charset)
        );
    }

    /**
     * 获取缓存的请求体内容
     */
    public String getCachedBody() {
        return new String(this.cachedBody, this.charset);
    }

    /**
     * 获取缓存的字节数组(副本)
     */
    public byte[] getCachedBodyBytes() {
        return this.cachedBody.clone();
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
            // 不支持异步也触发
            if (listener != null) {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        }

        @Override
        public int read() {
            return cachedBodyInputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return cachedBodyInputStream.read(b, off, len);
        }
    }
}