/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.impl;

import tech.smartboot.feat.core.common.*;
import tech.smartboot.feat.core.common.io.FeatOutputStream;
import tech.smartboot.feat.core.server.HttpResponse;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public abstract class AbstractResponse implements HttpResponse, Reset {
    /**
     * 输入流
     */
    protected FeatOutputStream outputStream;

    /**
     * 响应消息头
     */
    private Map<String, HeaderValue> headers = Collections.emptyMap();
    /**
     * http响应码
     */
    private HttpStatus httpStatus = HttpStatus.OK;


    /**
     * 响应正文长度
     */
    private long contentLength = -1;

    /**
     * 正文编码方式
     */
    private String contentType = HeaderValue.ContentType.TEXT_HTML_UTF8;

    /**
     * 是否关闭Socket连接通道
     */
    protected boolean closed = false;
    private boolean fastFlag = true;


    public final void reset() {
        outputStream.reset();
        headers.clear();
        setHttpStatus(HttpStatus.OK);
        contentType = HeaderValue.ContentType.TEXT_HTML_UTF8;
        contentLength = -1;
        this.closed = false;
        this.fastFlag = true;
    }


    public final FeatOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        Objects.requireNonNull(httpStatus);
        if (httpStatus.value() < 100 || httpStatus.value() > 1000) {
            throw new IllegalArgumentException("httpStatus must between 100 and 1000");
        }
        this.httpStatus = httpStatus;
        if (httpStatus.value() != HttpStatus.OK.value()) {
            fastFlag = false;
        }
    }


    public final void setHttpStatus(int value, String reasonPhrase) {
        setHttpStatus(new HttpStatus(value, Objects.requireNonNull(reasonPhrase)));
    }


    @Override
    public final void setHeader(String name, String value) {
        setHeader(name, value, true);
    }

    @Override
    public final void addHeader(String name, String value) {
        setHeader(name, value, false);
    }

    /**
     * @param name    header name
     * @param value   header value
     * @param replace true:replace,false:append
     */
    private void setHeader(String name, String value, boolean replace) {
        char cc = name.charAt(0);
        if (cc == 'C' || cc == 'c') {
            if (checkSpecialHeader(name, value)) return;
        }
        Map<String, HeaderValue> emptyHeaders = Collections.emptyMap();
        if (headers == emptyHeaders) {
            headers = new HashMap<>();
        }
        if (HeaderName.SERVER.getName().equals(name)) {
            fastFlag = false;
        }
        if (replace) {
            if (value == null) {
                headers.remove(name);
            } else {
                headers.put(name, new HeaderValue(null, value));
            }
            return;
        }

        HeaderValue headerValue = headers.get(name);
        if (headerValue == null) {
            setHeader(name, value, true);
            return;
        }
        HeaderValue preHeaderValue = null;
        while (headerValue != null && !headerValue.getValue().equals(value)) {
            preHeaderValue = headerValue;
            headerValue = headerValue.getNextValue();
        }
        if (headerValue == null) {
            preHeaderValue.setNextValue(new HeaderValue(null, value));
        }
    }

    /**
     * 部分header需要特殊处理
     */
    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase(HeaderName.CONTENT_TYPE.getName())) {
            setContentType(value);
            return true;
        } else if (name.equalsIgnoreCase(HeaderName.CONTENT_LENGTH.getName())) {
            setContentLength(Long.parseLong(value));
            return true;
        }
        return false;
    }

    @Override
    public void addCookie(Cookie cookie) {
        addHeader(HeaderName.SET_COOKIE, cookie.toString());
    }

    @Override
    public final String getHeader(String name) {
        HeaderValue headerValue = headers.get(name);
        return headerValue == null ? null : headerValue.getValue();
    }

    public final Map<String, HeaderValue> getHeaders() {
        return headers;
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        Vector<String> result = new Vector<>();
        HeaderValue headerValue = headers.get(name);
        while (headerValue != null) {
            result.addElement(headerValue.getValue());
            headerValue = headerValue.getNextValue();
        }
        return result;
    }

    @Override
    public final Collection<String> getHeaderNames() {
        return headers.keySet();
    }


    public final void write(byte[] buffer) throws IOException {
        outputStream.write(buffer);
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        outputStream.write(data, offset, length);
    }

    @Override
    public abstract void close();

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

    @Override
    public final void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 是否要断开TCP连接
     *
     * @return true/false
     */
    public final boolean isClosed() {
        return closed;
    }


    @Override
    public Supplier<Map<String, String>> getTrailerFields() {
        return outputStream.getTrailerFields();
    }

    boolean isFastFlag() {
        return fastFlag;
    }
}
