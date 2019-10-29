/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: DefaultHttpResponse.java
 * Date: 2018-02-17
 * Author: sandao
 */

package org.smartboot.http.server.http11;

import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
public class DefaultHttpResponse implements HttpResponse {

    private Map<String, String> headMap = new HashMap<>();
    /**
     * http响应码
     */
    private HttpStatus httpStatus;

    private HttpOutputStream outputStream;

    private int contentLength = -1;

    private String transferEncoding = null;

    private String contentType;

    public DefaultHttpResponse() {
        outputStream = new HttpOutputStream();
    }

    public void init(OutputStream outputStream) {
        this.outputStream.init(outputStream, this);
        headMap.clear();
        httpStatus = null;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public void setHeader(String name, String value) {
        headMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headMap.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headMap;
    }

    public void write(byte[] buffer) throws IOException {
        outputStream.write(buffer);
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
