/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.servlet;

import org.noear.solon.Utils;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.server.ServerProps;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.server.handle.AsyncContextState;
import org.noear.solon.server.handle.ContextBase;
import org.noear.solon.server.handle.HeaderNames;
import org.noear.solon.server.io.LimitedInputStream;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.server.util.RedirectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Servlet，适配为 Context
 *
 * @author noear
 * @since 1.2
 * @since 2.9
 * */
public class SolonServletContext extends ContextBase {
    static final Logger log = LoggerFactory.getLogger(SolonServletContext.class);

    private final HttpServletRequest _request;
    private final HttpServletResponse _response;
    private final boolean _useLimitStream;

    public SolonServletContext(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, false);
    }

    public SolonServletContext(HttpServletRequest request, HttpServletResponse response, boolean useLimitStream) {
        _request = request;
        _response = response;
        _useLimitStream = useLimitStream;

        if (sessionState().replaceable()) {
            sessionState = new SolonServletSessionState(request);
        }
    }

    private boolean _loadMultipartFormData = false;

    private void loadMultipartFormData() {
        if (_loadMultipartFormData) {
            return;
        } else {
            _loadMultipartFormData = true;
        }

        //文件上传需要
        if (isMultipartFormData()) {
            MultipartUtil.buildParamsAndFiles(this, _fileMap);
        }
    }

    @Override
    public boolean isHeadersSent() {
        return _headers_sent;
    }

    @Override
    public Object pull(Class<?> clz) {
        Object tmp = super.pull(clz);

        if (tmp == null) {
            if (HttpSession.class.isAssignableFrom(clz)) {
                return _request.getSession();
            }
        }

        return tmp;
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        return _request.getRemoteAddr();
    }

    @Override
    public int remotePort() {
        return _request.getRemotePort();
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getProtocol();
    }

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = this.parseURI(url());
        }

        return _uri;
    }

    @Override
    public boolean isSecure() {
        return _request.isSecure();
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            _url = _request.getRequestURL().toString();
        }

        return _url;
    }

    @Override
    public long contentLength() {
        return _request.getContentLengthLong();
    }

    @Override
    public String contentType() {
        return _request.getContentType();
    }


    @Override
    public String queryString() {
        return _request.getQueryString();
    }

    @Override
    public String body(String charset) throws IOException {
        try {
            return super.body(charset);
        } catch (Exception e) {
            throw MultipartUtil.status4xx(this, e);
        }
    }

    private InputStream limitStream;
    @Override
    public InputStream bodyAsStream() throws IOException {
        assertMaxBodySize();

        if (_useLimitStream) {
            if (limitStream == null) {
                limitStream = new LimitedInputStream(_request.getInputStream(), ServerProps.request_maxBodySize);
            }

            return limitStream;
        } else {
            return _request.getInputStream();
        }
    }


    private MultiMap<String> _paramMap;

    @Override
    public MultiMap<String> paramMap() {
        paramsMapInit();

        return _paramMap;
    }

    /**
     * @since 2.7
     * @since 2.9
     */
    private void paramsMapInit() {
        if (_paramMap == null) {
            _paramMap = new MultiMap<String>();

            try {
                if (isFormUrlencoded()) {
                    assertMaxBodySize();
                }

                //x-www-form-urlencoded(for put, delete ..)
                DecodeUtils.decodeFormUrlencoded(this);

                //try form-data
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                //queryString and x-www-form-urlencoded(for post)
                for (Map.Entry<String, String[]> kv : _request.getParameterMap().entrySet()) {
                    String name = ServerProps.urlDecode(kv.getKey());

                    _paramMap.holder(name).setValues(kv.getValue());
                }
            } catch (Exception e) {
                throw MultipartUtil.status4xx(this, e);
            }
        }
    }

    /**
     * @since 3.6
     */
    protected void assertMaxBodySize() {
        if (_request.getContentLengthLong() > ServerProps.request_maxBodySize) {
            //可兼容不同框架的情况
            throw new StatusException("Request Entity Too Large: " + _request.getContentLengthLong(), 413);
        }
    }

    @Override
    public MultiMap<UploadedFile> fileMap() {
        if (isMultipartFormData()) {
            loadMultipartFormData();
        }

        return _fileMap;
    }

    private MultiMap<String> _cookieMap;

    @Override
    public MultiMap<String> cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new MultiMap<>(false);

            //_request.cookies() 可能不支持多个同名 cookie
            DecodeUtils.decodeCookies(this, header(HeaderNames.HEADER_COOKIE));
        }

        return _cookieMap;
    }

    @Override
    public MultiMap<String> headerMap() {
        if (_headerMap == null) {
            _headerMap = new MultiMap<String>();
            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                _headerMap.holder(key).setValues(Collections.list(_request.getHeaders(key)));
            }
        }

        return _headerMap;
    }

    private MultiMap<String> _headerMap;

    //====================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _response.setCharacterEncoding(charset);
        this.charset = Charset.forName(charset);
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        _response.setContentType(contentType);
    }


    @Override
    public OutputStream outputStream() throws IOException {
        sendHeaders();

        return _response.getOutputStream();
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = outputStream();

            out.write(bytes);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = outputStream();

            IoUtil.transferTo(stream, out);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void headerSet(String key, String val) {
        _response.setHeader(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.addHeader(key, val);
    }


    @Override
    public String headerOfResponse(String name) {
        return _response.getHeader(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        return _response.getHeaders(name);
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        return _response.getHeaderNames();
    }

    @Override
    public void cookieSet(org.noear.solon.core.handle.Cookie cookie) {
        Cookie c = new Cookie(cookie.name, cookie.value);

        if (cookie.maxAge >= 0) {
            c.setMaxAge(cookie.maxAge);
        }

        if (Utils.isNotEmpty(cookie.domain)) {
            c.setDomain(cookie.domain);
        }

        if (Utils.isNotEmpty(cookie.path)) {
            c.setPath(cookie.path);
        }

        c.setSecure(cookie.secure);
        c.setHttpOnly(cookie.httpOnly);

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url, int code) {
        url = RedirectUtils.getRedirectPath(url);

        headerSet(HeaderNames.HEADER_LOCATION, url);
        statusDoSet(code);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    protected void statusDoSet(int status) {
        _response.setStatus(status);
    }


    @Override
    public void flush() throws IOException {
        outputStream().flush();
    }

    @Override
    public void close() throws IOException {
        outputStream().close();
    }

    @Override
    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            sendHeaders();
        } else {
            _response.setStatus(404);
        }
    }

    private boolean _headers_sent = false;

    private void sendHeaders() throws IOException {
        if (!_headers_sent) {
            _headers_sent = true;

            if (sessionState() != null) {
                sessionState().sessionPublish();
            }
        }
    }

    ///////////////////////
    // for async
    ///////////////////////

    protected final AsyncContextState asyncState = new AsyncContextState();
    private   AsyncContext asyncContext;

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public boolean asyncStarted() {
        return asyncContext != null;
    }

    @Override
    public void asyncListener(ContextAsyncListener listener) {
        asyncState.addListener(listener);
    }

    @Override
    public void asyncStart(long timeout, Runnable runnable) {
        if (asyncContext == null) {
            asyncContext = _request.startAsync();
            asyncState.isStarted = true;

            asyncContext.addListener(new AsyncListenerWrap(this, asyncState));

            if (timeout != 0) {
                //内部默认30秒
                asyncContext.setTimeout(timeout);
            }

            if (runnable != null) {
                asyncContext.start(runnable);
            }
        }
    }

    @Override
    public void asyncComplete() {
        if (asyncContext != null) {
            try {
                innerCommit();
            } catch (Throwable e) {
                log.warn("Async completion failed", e);
                asyncState.onError(this, e);
            } finally {
                asyncContext.complete();
            }
        }
    }
}