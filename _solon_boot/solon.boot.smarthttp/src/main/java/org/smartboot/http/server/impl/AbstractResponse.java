/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: AbstractResponse.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.impl;

import org.smartboot.http.common.Cookie;
import org.smartboot.http.common.HeaderValue;
import org.smartboot.http.common.Reset;
import org.smartboot.http.common.enums.HeaderNameEnum;
import org.smartboot.http.common.enums.HeaderValueEnum;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.server.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
class AbstractResponse implements HttpResponse, Reset {
    /**
     * 输入流
     */
    private AbstractOutputStream outputStream;

    /**
     * 响应消息头
     */
    private Map<String, HeaderValue> headers = Collections.emptyMap();
    /**
     * http响应码
     */
    private int httpStatus = HttpStatus.OK.value();
    /**
     * 响应描述
     */
    private String reasonPhrase = HttpStatus.OK.getReasonPhrase();
    /**
     * 是否默认响应
     */
    private boolean defaultStatus = true;
    /**
     * 响应正文长度
     */
    private int contentLength = -1;

    /**
     * 正文编码方式
     */
    private String contentType = HeaderValueEnum.DEFAULT_CONTENT_TYPE.getName();

    private AbstractRequest request;

    private String characterEncoding;
    /**
     * 是否关闭Socket连接通道
     */
    private boolean closed = false;

    /**
     * 是否启用压缩模式
     */
    private boolean gzip = false;


    private List<Cookie> cookies = Collections.emptyList();

    protected void init(AbstractRequest request, AbstractOutputStream outputStream) {
        this.request = request;
        this.outputStream = outputStream;
    }


    public final void reset() {
        outputStream.reset();
        headers.clear();
        setHttpStatus(HttpStatus.OK);
        contentType = HeaderValueEnum.DEFAULT_CONTENT_TYPE.getName();
        contentLength = -1;
        characterEncoding = null;
        cookies = Collections.emptyList();
        this.closed = false;
        gzip = false;
    }


    public final AbstractOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    public final void setHttpStatus(HttpStatus httpStatus) {
        Objects.requireNonNull(httpStatus);
        setHttpStatus(httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public final void setHttpStatus(int value, String reasonPhrase) {
        this.httpStatus = value;
        this.reasonPhrase = Objects.requireNonNull(reasonPhrase);
        defaultStatus = httpStatus == HttpStatus.OK.value() && HttpStatus.OK.getReasonPhrase().equals(reasonPhrase);
    }

    public boolean isDefaultStatus() {
        return defaultStatus;
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
        if (name.equalsIgnoreCase(HeaderNameEnum.CONTENT_TYPE.getName())) {
            setContentType(value);
            return true;
        } else if (name.equalsIgnoreCase(HeaderNameEnum.CONTENT_ENCODING.getName())) {
            gzip = HeaderValueEnum.GZIP.getName().equals(value);
        } else if (name.equalsIgnoreCase(HeaderNameEnum.CONTENT_LENGTH.getName())) {
            setContentLength(Integer.parseInt(value));
            return true;
        }
        return false;
    }

    @Override
    public final String getHeader(String name) {
        HeaderValue headerValue = headers.get(name);
        return headerValue == null ? null : headerValue.getValue();
    }

    final Map<String, HeaderValue> getHeaders() {
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
        return new ArrayList<>(headers.keySet());
    }


    public final void write(byte[] buffer) throws IOException {
        outputStream.write(buffer);
    }

    @Override
    public final String getCharacterEncoding() {
        return characterEncoding == null ? request.getCharacterEncoding() : characterEncoding;
    }

    @Override
    public final void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public final void close() {
        if (closed) {
            return;
        }
        try {
            if (outputStream != null && !outputStream.isClosed()) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            request.getRequest().getAioSession().close(false);
        }
        closed = true;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public void addCookie(Cookie cookie) {
        List<Cookie> emptyList = Collections.emptyList();
        if (cookies == emptyList) {
            cookies = new ArrayList<>();
        }
        cookies.add(cookie);
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

    @Override
    public final void setContentType(String contentType) {
        this.contentType = Objects.requireNonNull(contentType);
    }

    /**
     * 是否要断开TCP连接
     *
     * @return true/false
     */
    public final boolean isClosed() {
        return closed;
    }

    public boolean isGzip() {
        return gzip;
    }
}
