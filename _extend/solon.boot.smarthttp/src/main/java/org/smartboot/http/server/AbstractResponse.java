/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Http11Response.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    private Map<String, HeaderValue> headers = null;
    /**
     * http响应码
     */
    private HttpStatus httpStatus;
    /**
     * 响应正文长度
     */
    private int contentLength = -1;

    /**
     * 正文编码方式
     */
    private String contentType;

    private HttpRequest request;

    private String characterEncoding;
    /**
     * 是否关闭Socket连接通道
     */
    private boolean closed = false;

    protected void init(HttpRequest request, AbstractOutputStream outputStream) {
        this.request = request;
        this.outputStream = outputStream;
    }


    public final void reset() {
        outputStream.reset();
        if (headers != null) {
            headers.clear();
        }
        httpStatus = null;
        contentType = null;
        contentLength = -1;
        characterEncoding = null;
        this.closed = false;
    }


    public final AbstractOutputStream getOutputStream() {
        return outputStream;
    }

    public final HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public final void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
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
            if (checkSpecialHeader(name, value))
                return;
        }

        if (headers == null) {
            headers = new HashMap<>();
        }
        if (replace) {
            headers.put(name, new HeaderValue(null, value));
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
        if (name.equalsIgnoreCase("Content-Type")) {
            setContentType(value);
            return true;
        }
        return false;
    }

    @Override
    public final String getHeader(String name) {
        HeaderValue headerValue = null;
        if (headers != null) {
            headerValue = headers.get(name);
        }
        return headerValue == null ? null : headerValue.getValue();
    }

    final Map<String, HeaderValue> getHeaders() {
        return headers;
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        if (headers == null) {
            return Collections.emptyList();
        }
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
        if (headers == null) {
            return Collections.emptyList();
        }
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
        }
        closed = true;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public final String getContentType() {
        return contentType;
    }

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
}
