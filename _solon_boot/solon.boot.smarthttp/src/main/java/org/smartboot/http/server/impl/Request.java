/*******************************************************************************
 * Copyright (c) 2017-2021, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Request.java
 * Date: 2021-02-07
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server.impl;

import org.smartboot.http.common.Cookie;
import org.smartboot.http.common.HeaderValue;
import org.smartboot.http.common.Reset;
import org.smartboot.http.common.enums.DecodePartEnum;
import org.smartboot.http.common.enums.HeaderNameEnum;
import org.smartboot.http.common.enums.HttpTypeEnum;
import org.smartboot.http.common.utils.ByteTree;
import org.smartboot.http.common.utils.Constant;
import org.smartboot.http.common.utils.HttpUtils;
import org.smartboot.http.common.utils.NumberUtils;
import org.smartboot.http.common.utils.StringUtils;
import org.smartboot.http.server.Http2ServerHandler;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpServerConfiguration;
import org.smartboot.http.server.ServerHandler;
import org.smartboot.http.server.WebSocketHandler;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.util.Attachment;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/31
 */
public final class Request implements HttpRequest, Reset {
    private static final Locale defaultLocale = Locale.getDefault();
    private static final int INIT_CONTENT_LENGTH = -2;
    private static final int NONE_CONTENT_LENGTH = -1;
    private final AioSession aioSession;
    /**
     * Http请求头
     */
    private final List<HeaderValue> headers = new ArrayList<>(8);
    private final HttpServerConfiguration configuration;
    private ByteTree<Function<String, ServerHandler>> headerTemp;
    /**
     * 请求参数
     */
    private Map<String, String[]> parameters;
    /**
     * 原始的完整请求
     */
    private String uri;
    private int headerSize = 0;
    /**
     * 请求方法
     */
    private String method;
    /**
     * Http协议版本
     */
    private String protocol;
    private String requestUri;
    private String requestUrl;
    private String contentType;
    /**
     * 跟在URL后面的请求信息
     */
    private String queryString;
    /**
     * 协议
     */
    private String scheme = Constant.SCHEMA_HTTP;
    private int contentLength = INIT_CONTENT_LENGTH;
    private String remoteAddr;
    private String remoteHost;
    private String hostHeader;
    /**
     * 消息类型
     */
    private HttpTypeEnum type = null;
    /**
     * Post表单
     */
    private String formUrlencoded;
    private Cookie[] cookies;
    /**
     * 附件对象
     */
    private Attachment attachment;
    private DecodePartEnum decodePartEnum = DecodePartEnum.HEADER_FINISH;
    private HttpRequestImpl httpRequest;
    private WebSocketRequestImpl webSocketRequest;
    private ServerHandler serverHandler;

    Request(HttpServerConfiguration configuration, AioSession aioSession) {
        this.configuration = configuration;
        this.aioSession = aioSession;
    }

    public DecodePartEnum getDecodePartEnum() {
        return decodePartEnum;
    }

    public void setDecodePartEnum(DecodePartEnum decodePartEnum) {
        this.decodePartEnum = decodePartEnum;
    }


    public AioSession getAioSession() {
        return aioSession;
    }

    public final String getHost() {
        if (hostHeader == null) {
            hostHeader = getHeader(HeaderNameEnum.HOST.getName());
        }
        return hostHeader;
    }

    @Override
    public final String getHeader(String headName) {
        for (int i = 0; i < headerSize; i++) {
            HeaderValue headerValue = headers.get(i);
            if (headerValue.getName().equalsIgnoreCase(headName)) {
                return headerValue.getValue();
            }
        }
        return null;
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        List<String> value = new ArrayList<>(4);
        for (int i = 0; i < headerSize; i++) {
            HeaderValue headerValue = headers.get(i);
            if (headerValue.getName().equalsIgnoreCase(name)) {
                value.add(headerValue.getValue());
            }
        }
        return value;
    }

    @Override
    public final Collection<String> getHeaderNames() {
        Set<String> nameSet = new HashSet<>();
        for (int i = 0; i < headerSize; i++) {
            nameSet.add(headers.get(i).getName());
        }
        return nameSet;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    public final void setHeadValue(String value) {
        if (headerTemp.getAttach() != null) {
            ServerHandler replaceServerHandler = headerTemp.getAttach().apply(value);
            if (replaceServerHandler != null) {
                setServerHandler(replaceServerHandler);
            }
        }
        setHeader(headerTemp.getStringValue(), value);
    }

    public final void setHeader(String headerName, String value) {
        if (headerSize < headers.size()) {
            HeaderValue headerValue = headers.get(headerSize);
            headerValue.setName(headerName);
            headerValue.setValue(value);
        } else {
            headers.add(new HeaderValue(headerName, value));
        }
        headerSize++;
    }

    public HttpTypeEnum getRequestType() {
        if (type != null) {
            return type;
        }
        if (serverHandler instanceof WebSocketHandler) {
            type = HttpTypeEnum.WEBSOCKET;
        } else if (serverHandler instanceof Http2ServerHandler) {
            type = HttpTypeEnum.HTTP_2;
        } else {
            type = HttpTypeEnum.HTTP;
        }
        return type;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public final String getRequestURI() {
        return requestUri;
    }

    public final void setRequestURI(String uri) {
        this.requestUri = uri;
    }

    @Override
    public final String getProtocol() {
        return protocol;
    }

    public final void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public final String getMethod() {
        return method;
    }

    public final void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public final void setUri(String uri) {
        this.uri = uri;
    }

    public void setHeaderTemp(ByteTree headerTemp) {
        this.headerTemp = headerTemp;
    }

    @Override
    public final String getRequestURL() {
        if (requestUrl != null) {
            return requestUrl;
        }
        if (requestUri.startsWith("/")) {
            requestUrl = getScheme() + "://" + getHeader(HeaderNameEnum.HOST.getName()) + getRequestURI();
        } else {
            requestUrl = requestUri;
        }
        return requestUrl;
    }

    public final String getScheme() {
        return scheme;
    }

    public final void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public final String getQueryString() {
        return queryString;
    }

    public final void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public final String getContentType() {
        if (contentType != null) {
            return contentType;
        }
        contentType = getHeader(HeaderNameEnum.CONTENT_TYPE.getName());
        return contentType;
    }

    @Override
    public final int getContentLength() {
        if (contentLength > INIT_CONTENT_LENGTH) {
            return contentLength;
        }
        //不包含content-length,则为：-1
        contentLength = NumberUtils.toInt(getHeader(HeaderNameEnum.CONTENT_LENGTH.getName()), NONE_CONTENT_LENGTH);
        return contentLength;
    }

    @Override
    public final String getParameter(String name) {
        String[] arr = (name != null ? getParameterValues(name) : null);
        return (arr != null && arr.length > 0 ? arr[0] : null);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (parameters != null) {
            return parameters.get(name);
        }
        parameters = new HashMap<>();
        //识别url中的参数
        String urlParamStr = queryString;
        if (StringUtils.isNotBlank(urlParamStr)) {
            urlParamStr = StringUtils.substringBefore(urlParamStr, "#");
            HttpUtils.decodeParamString(urlParamStr, parameters);
        }

        if (formUrlencoded != null) {
            HttpUtils.decodeParamString(formUrlencoded, parameters);
        }
        return getParameterValues(name);
    }

    @Override
    public final Map<String, String[]> getParameters() {
        if (parameters == null) {
            getParameter("");
        }
        return parameters;
    }

    /**
     * Returns the Internet Protocol (IP) address of the client
     * or last proxy that sent the request.
     * For HTTP servlets, same as the value of the
     * CGI variable <code>REMOTE_ADDR</code>.
     *
     * @return a <code>String</code> containing the
     * IP address of the client that sent the request
     */
    @Override
    public final String getRemoteAddr() {
        if (remoteAddr != null) {
            return remoteAddr;
        }
        try {
            InetSocketAddress remote = aioSession.getRemoteAddress();
            InetAddress address = remote.getAddress();
            if (address == null) {
                remoteAddr = remote.getHostString();
            } else {
                remoteAddr = address.getHostAddress();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return remoteAddr;
    }

    @Override
    public final InetSocketAddress getRemoteAddress() {
        try {
            return aioSession.getRemoteAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public final InetSocketAddress getLocalAddress() {
        try {
            return aioSession.getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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
    @Override
    public final String getRemoteHost() {
        if (remoteHost != null) {
            return remoteHost;
        }
        try {
            remoteHost = aioSession.getRemoteAddress().getHostString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return remoteHost;
    }

    @Override
    public final Locale getLocale() {
        return defaultLocale;
    }

    @Override
    public final Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singletonList(defaultLocale));
    }

    @Override
    public final String getCharacterEncoding() {
        return "utf8";
    }

    @Override
    public Cookie[] getCookies() {
        if (cookies != null) {
            return cookies;
        }
        String cookieValue = getHeader(HeaderNameEnum.COOKIE.getName());
        if (StringUtils.isBlank(cookieValue)) {
            return new Cookie[0];
        }
        List<Cookie> cookieList = new ArrayList<>();

        int state = 1;// 1:name ,2:value
        int startIndex = 0;
        int endIndex = 0;
        String name = null;
        String value;
        for (int i = 0; i < cookieValue.length(); i++) {
            switch (state) {
                case 1:
                    //查找name
                    if (cookieValue.charAt(i) == '=') {
                        endIndex = i;
                        while (cookieValue.charAt(startIndex) == ' ' && startIndex < i) {
                            startIndex++;
                        }
                        while (cookieValue.charAt(endIndex - 1) == ' ' && endIndex > startIndex) {
                            endIndex--;
                        }
                        name = cookieValue.substring(startIndex, endIndex);
                        startIndex = i + 1;
                        state = 2;
                    }
                    break;
                case 2:
                    //查找value
                    if (cookieValue.charAt(i) == ';') {
                        endIndex = i;
                        while (cookieValue.charAt(startIndex) == ' ' && startIndex < i) {
                            startIndex++;
                        }
                        while (cookieValue.charAt(endIndex - 1) == ' ' && endIndex > startIndex) {
                            endIndex--;
                        }
                        value = cookieValue.substring(startIndex, endIndex);
                        cookieList.add(new Cookie(name, value));
                        startIndex = i + 1;
                        state = 1;
                    }
                    break;
            }
        }
        //最后一个value为空字符串情况
        if (startIndex == cookieValue.length()) {
            cookieList.add(new Cookie(name, ""));
        } else if (endIndex < startIndex) {
            endIndex = cookieValue.length();
            while (cookieValue.charAt(startIndex) == ' ') {
                startIndex++;
            }
            while (cookieValue.charAt(endIndex - 1) == ' ' && endIndex > startIndex) {
                endIndex--;
            }
            value = cookieValue.substring(startIndex, endIndex);
            cookieList.add(new Cookie(name, value));
        }
        cookies = new Cookie[cookieList.size()];
        cookieList.toArray(cookies);
        return cookies;
    }

    public String getFormUrlencoded() {
        return formUrlencoded;
    }

    public void setFormUrlencoded(String formUrlencoded) {
        this.formUrlencoded = formUrlencoded;
    }

    /**
     * 获取附件对象
     *
     * @param <A> 附件对象类型
     * @return 附件
     */
    public Attachment getAttachment() {
        return attachment;
    }

    /**
     * 存放附件，支持任意类型
     *
     * @param <A>        附件对象类型
     * @param attachment 附件对象
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }


    public AbstractRequest newAbstractRequest() {
        switch (getRequestType()) {
            case WEBSOCKET:
                return newWebsocketRequest();
            case HTTP:
                return newHttpRequest();
            default:
                return null;
        }
    }

    public HttpRequestImpl newHttpRequest() {
        if (httpRequest == null) {
            httpRequest = new HttpRequestImpl(this);
        }
        return httpRequest;
    }

    public WebSocketRequestImpl newWebsocketRequest() {
        if (webSocketRequest == null) {
            webSocketRequest = new WebSocketRequestImpl(this);
        }
        return webSocketRequest;
    }

    public HttpServerConfiguration getConfiguration() {
        return configuration;
    }

    public void reset() {
        headerSize = 0;
        method = null;
        uri = null;
        requestUrl = null;
        parameters = null;
        contentType = null;
        contentLength = INIT_CONTENT_LENGTH;
        formUrlencoded = null;
        queryString = null;
        cookies = null;
        httpRequest = null;
        webSocketRequest = null;
        type = null;
        decodePartEnum = DecodePartEnum.HEADER_FINISH;
    }
}
