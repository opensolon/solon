/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Http11Request.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http.server;

import org.smartboot.http.HttpRequest;
import org.smartboot.http.enums.YesNoEnum;
import org.smartboot.http.utils.CharArray;
import org.smartboot.http.utils.Constant;
import org.smartboot.http.utils.HttpHeaderConstant;
import org.smartboot.http.utils.NumberUtils;
import org.smartboot.http.utils.StringUtils;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private final CharArray headerValueCache = new CharArray(16 * 1024);

    private String headerTemp;
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


    private YesNoEnum websocket = null;

    /**
     * Post表单
     */
    private String formUrlencoded;

    Request(AioSession aioSession) {
        this.aioSession = aioSession;
    }

    public AioSession getAioSession() {
        return aioSession;
    }

    public final String getHost() {
        if (hostHeader == null) {
            hostHeader = getHeader(HttpHeaderConstant.Names.HOST);
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

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setHeader(int start, int length) {
        if (headerSize < headers.size()) {
            HeaderValue headerValue = headers.get(headerSize);
            headerValue.setName(headerTemp);
            headerValue.setValue(null);
            headerValue.setValue(start, length);
        } else {
            HeaderValue headerValue = new HeaderValue(headerTemp, null);
            headerValue.setValue(start, length);
            headerValue.setCharArray(headerValueCache.getData());
            headers.add(headerValue);
        }
        headerSize++;
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

    public String getHeaderTemp() {
        return headerTemp;
    }

    public void setHeaderTemp(String headerTemp) {
        this.headerTemp = headerTemp;
    }

    @Override
    public final String getRequestURL() {
        if (requestUrl != null) {
            return requestUrl;
        }
        if (requestUri.startsWith("/")) {
            requestUrl = getScheme() + "://" + getHeader(HttpHeaderConstant.Names.HOST) + getRequestURI();
        } else {
            requestUrl = requestUri;
        }
        return requestUrl;
    }

    public final String getScheme() {
        return scheme;
    }

    final void setScheme(String scheme) {
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
        contentType = getHeader(HttpHeaderConstant.Names.CONTENT_TYPE);
        return contentType;
    }

    @Override
    public final int getContentLength() {
        if (contentLength > INIT_CONTENT_LENGTH) {
            return contentLength;
        }
        //不包含content-length,则为：-1
        contentLength = NumberUtils.toInt(getHeader(HttpHeaderConstant.Names.CONTENT_LENGTH), NONE_CONTENT_LENGTH);
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
            decodeParamString(urlParamStr, parameters);
        }

        if (formUrlencoded != null) {
            decodeParamString(formUrlencoded, parameters);
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
        return Collections.enumeration(Arrays.asList(defaultLocale));
    }

    @Override
    public final String getCharacterEncoding() {
        return "utf8";
    }

    void decodeParamString(String paramStr, Map<String, String[]> paramMap) {
        if (StringUtils.isBlank(paramStr)) {
            return;
        }
        String[] uriParamStrArray = StringUtils.split(paramStr, "&");
        for (String param : uriParamStrArray) {
            int index = param.indexOf("=");
            if (index == -1) {
                continue;
            }
            try {
                String key = StringUtils.substring(param, 0, index);
                String value = URLDecoder.decode(StringUtils.substring(param, index + 1), "utf8");
                String[] values = paramMap.get(key);
                if (values == null) {
                    paramMap.put(key, new String[]{value});
                } else {
                    String[] newValue = new String[values.length + 1];
                    System.arraycopy(values, 0, newValue, 0, values.length);
                    newValue[values.length] = value;
                    paramMap.put(key, newValue);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public final YesNoEnum isWebsocket() {
        return websocket;
    }

    public final void setWebsocket(YesNoEnum websocket) {
        this.websocket = websocket;
    }

    public String getFormUrlencoded() {
        return formUrlencoded;
    }

    public void setFormUrlencoded(String formUrlencoded) {
        this.formUrlencoded = formUrlencoded;
    }

    public CharArray getHeaderValueCache() {
        return headerValueCache;
    }

    public void reset() {
        headerValueCache.setWriteIndex(0);
        headerSize = 0;
        method = null;
        uri = null;
        requestUrl = null;
        parameters = null;
        contentType = null;
        contentLength = INIT_CONTENT_LENGTH;
        formUrlencoded = null;
        queryString = null;
    }
}
