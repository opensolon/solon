/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: HttpRequest.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Http消息请求接口
 *
 * @author 三刀
 * @version V1.0 , 2018/8/7
 */
public interface HttpRequest {
    String HTTP_1_0_STRING = "HTTP/1.0";
    String HTTP_1_1_STRING = "HTTP/1.1";

    /**
     * 获取指定名称的Http Header值
     *
     * @param headName
     * @return
     */
    String getHeader(String headName);


    public Collection<String> getHeaders(String name);

    Collection<String> getHeaderNames();

    InputStream getInputStream() throws IOException;

    String getRequestURI();

    String getProtocol();

    /**
     * Returns the name of the HTTP method with which this
     * request was made, for example, GET, POST, or PUT.
     * Same as the value of the CGI variable REQUEST_METHOD.
     *
     * @return a <code>String</code>
     * specifying the name
     * of the method with which
     * this request was made
     */
    String getMethod();

    String getScheme();

    String getRequestURL();

    String getQueryString();

    String getContentType();

    int getContentLength();

    String getParameter(String name);

    String[] getParameterValues(String name);

    Map<String, String[]> getParameters();

    /**
     * Returns the Internet Protocol (IP) address of the client
     * or last proxy that sent the request.
     * For HTTP servlets, same as the value of the
     * CGI variable <code>REMOTE_ADDR</code>.
     *
     * @return a <code>String</code> containing the
     * IP address of the client that sent the request
     */
    String getRemoteAddr();

    InetSocketAddress getRemoteAddress();

    InetSocketAddress getLocalAddress();

    /**
     * Returns the fully qualified name of the client
     * or the last proxy that sent the request.
     * If the engine cannot or chooses not to resolve the hostname
     * (to improve performance), this method returns the dotted-string form of
     * the IP address. For HTTP servlets, same as the value of the CGI variable
     * <code>REMOTE_HOST</code>.
     *
     * @return a <code>String</code> containing the fully
     * qualified name of the client
     */
    String getRemoteHost();

    public Locale getLocale();

    public Enumeration<Locale> getLocales();

    String getCharacterEncoding();

}
