/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpOutputStream.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.enums.HttpStatus;
import org.smartboot.http.utils.HttpHeaderConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
final class HttpOutputStream extends AbstractOutputStream {
    /**
     * key:status+contentType
     */
    private static final Map<String, byte[]>[] CACHE_CONTENT_TYPE_AND_LENGTH = new Map[512];
    /**
     * Key：status+contentType
     */
    private static final Map<String, byte[]> CACHE_CHUNKED_AND_LENGTH = new HashMap<>();

    static {
        for (int i = 0; i < CACHE_CONTENT_TYPE_AND_LENGTH.length; i++) {
            CACHE_CONTENT_TYPE_AND_LENGTH[i] = new HashMap<>();
        }
    }

    private final OutputStream outputStream;

    public HttpOutputStream(HttpRequestImpl request, HttpResponseImpl response, OutputStream outputStream) {
        super(request, response, outputStream);
        this.outputStream = outputStream;
    }

    /**
     * 输出Http消息头
     *
     * @throws IOException
     */
    final protected void writeHead() throws IOException {
        if (committed) {
            return;
        }
        if (response.getHttpStatus() == null) {
            response.setHttpStatus(HttpStatus.OK);
        }
        String contentType = response.getContentType();
        if (contentType == null) {
            contentType = HttpHeaderConstant.Values.DEFAULT_CONTENT_TYPE;
        }

        //输出http状态行、contentType,contentLength、Transfer-Encoding、server等信息
        outputStream.write(getHeadPart(response.getHttpStatus(), contentType, response.getContentLength()));

        //输出Header部分
        if (response.getHeaders() != null) {
            for (Map.Entry<String, HeaderValue> entry : response.getHeaders().entrySet()) {
                HeaderValue headerValue = entry.getValue();
                while (headerValue != null) {
                    outputStream.write(getHeaderNameBytes(entry.getKey()));
                    outputStream.write(getBytes(headerValue.getValue()));
                    headerValue = headerValue.getNextValue();
                }
            }
        }

        /**
         * RFC2616 3.3.1
         * 只能用 RFC 1123 里定义的日期格式来填充头域 (header field)的值里用到 HTTP-date 的地方
         */
        flushDate();
        outputStream.write(date);
        committed = true;
    }

    private byte[] getHeadPart(HttpStatus httpStatus, String contentType, int contentLength) {
        chunked = contentLength < 0;
        byte[] data = null;
        //成功消息优先从缓存中加载
        if (httpStatus == HttpStatus.OK) {
            if (chunked) {
                data = CACHE_CHUNKED_AND_LENGTH.get(contentType);
            } else if (contentLength < CACHE_CONTENT_TYPE_AND_LENGTH.length) {
                data = CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].get(contentType);
            }
            if (data != null) {
                return data;
            }
        }

        String str = httpStatus.getHttpStatusLine() + "\r\n"
                + HttpHeaderConstant.Names.CONTENT_TYPE + ":" + contentType;
        if (contentLength >= 0) {
            str += "\r\n" + HttpHeaderConstant.Names.CONTENT_LENGTH + ":" + contentLength;
        } else if (chunked) {
            str += "\r\n" + HttpHeaderConstant.Names.TRANSFER_ENCODING + ":" + HttpHeaderConstant.Values.CHUNKED;
        }
        data = str.getBytes();
        //缓存响应头
        if (httpStatus == HttpStatus.OK) {
            if (chunked) {
                CACHE_CHUNKED_AND_LENGTH.put(contentType, data);
            } else if (contentLength >= 0 && contentLength < CACHE_CONTENT_TYPE_AND_LENGTH.length) {
                CACHE_CONTENT_TYPE_AND_LENGTH[contentLength].put(contentType, data);
            }
        }
        return data;
    }

}
