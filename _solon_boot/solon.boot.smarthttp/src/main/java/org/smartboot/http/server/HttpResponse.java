/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpResponse.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.common.BufferOutputStream;
import org.smartboot.http.common.Cookie;
import org.smartboot.http.common.enums.HttpStatus;

import java.io.IOException;
import java.util.Collection;

/**
 * Http消息响应接口
 *
 * @author 三刀
 * @version V1.0 , 2018/2/3
 */
public interface HttpResponse {

    /**
     * 响应消息输出流
     *
     * @return
     */
    BufferOutputStream getOutputStream();

    /**
     * 获取Http响应状态
     *
     * @return
     */
    int getHttpStatus();

    /**
     * 设置Http响应状态,若不设置默认{@link HttpStatus#OK}
     *
     * @param httpStatus
     */
    void setHttpStatus(HttpStatus httpStatus);

    /**
     * 设置Http响应状态,若不设置默认{@link HttpStatus#OK}
     *
     * @param httpStatus
     */
    void setHttpStatus(int httpStatus, String reasonPhrase);

    /**
     * 获取Http响应描述
     *
     * @return
     */
    String getReasonPhrase();

    /**
     * Sets a response header with the given name and value. If the header had
     * already been set, the new value overwrites the previous one. The
     * <code>containsHeader</code> method can be used to test for the presence
     * of a header before setting its value.
     *
     * @param name  the name of the header
     * @param value the header value If it contains octet string, it should be
     *              encoded according to RFC 2047
     *              (http://www.ietf.org/rfc/rfc2047.txt)
     * @see #addHeader
     */
    public void setHeader(String name, String value);

    /**
     * Adds a response header with the given name and value. This method allows
     * response headers to have multiple values.
     *
     * @param name  the name of the header
     * @param value the additional header value If it contains octet string, it
     *              should be encoded according to RFC 2047
     *              (http://www.ietf.org/rfc/rfc2047.txt)
     * @see #setHeader
     */
    public void addHeader(String name, String value);

    String getHeader(String name);

    /**
     * Return a Collection of all the header values associated with the
     * specified header name.
     *
     * @param name Header name to look up
     * @return The values for the specified header. These are the raw values so
     * if multiple values are specified in a single header that will be
     * returned as a single header value.
     * @since Servlet 3.0
     */
    public Collection<String> getHeaders(String name);

    /**
     * Get the header names set for this HTTP response.
     *
     * @return The header names set for this HTTP response.
     * @since Servlet 3.0
     */
    public Collection<String> getHeaderNames();

    void setContentLength(int contentLength);

    int  getContentLength();

    void setContentType(String contentType);

    String getContentType();

    void write(byte[] data) throws IOException;

    public String getCharacterEncoding();

    public void setCharacterEncoding(String charset);

    public void close();

    /**
     * 添加Cookie信息
     *
     * @param cookie
     */
    void addCookie(Cookie cookie);
}
