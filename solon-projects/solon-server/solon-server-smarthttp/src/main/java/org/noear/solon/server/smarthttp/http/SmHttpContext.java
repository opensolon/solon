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
package org.noear.solon.server.smarthttp.http;

import org.noear.solon.server.ServerProps;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.server.handle.AsyncContextState;
import org.noear.solon.server.handle.ContextBase;
import org.noear.solon.server.handle.HeaderNames;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.server.util.RedirectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.http.common.Cookie;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpResponse;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SmHttpContext extends ContextBase {
    static final Logger log = LoggerFactory.getLogger(SmHttpContext.class);

    private HttpRequest _request;
    private HttpResponse _response;


    protected HttpRequest innerGetRequest() {
        return _request;
    }

    protected HttpResponse innerGetResponse() {
        return _response;
    }


    private CompletableFuture<Object> _future;

    public SmHttpContext(HttpRequest request, HttpResponse response, CompletableFuture<Object> future) {
        _request = request;
        _response = response;
        _future = future;
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
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        return _request.getRemoteAddr();
    }

    @Override
    public int remotePort() {
        return _request.getRemoteAddress().getPort();
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getProtocol().getProtocol();
    }

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(url());
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
            _url = _request.getRequestURL();
        }

        return _url;
    }

    @Override
    public long contentLength() {
        return _request.getContentLength();
    }

    private String queryString;

    @Override
    public String queryString() {
        try {
            if (queryString == null) {
                queryString = _request.getQueryString();

                if (queryString == null) {
                    queryString = "";
                } else {
                    queryString = ServerProps.urlDecode(queryString);
                }
            }

            return queryString;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String body(String charset) throws IOException {
        try {
            return super.body(charset);
        } catch (Exception e) {
            throw MultipartUtil.status4xx(this, e);
        }
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
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
                //编码窗体预处理（不再需要了）
                //DecodeUtils.decodeFormUrlencoded(this);

                //多分段处理
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (Map.Entry<String, String[]> entry : _request.getParameters().entrySet()) {
                    String key = ServerProps.urlDecode(entry.getKey());
                    _paramMap.holder(key).setValues(entry.getValue());
                }
            } catch (Exception e) {
                throw MultipartUtil.status4xx(this, e);
            }
        }
    }

    @Override
    public MultiMap<UploadedFile> fileMap() {
        if (isMultipartFormData()) {
            loadMultipartFormData();
        }

        return _fileMap;
    }

    @Override
    public MultiMap<String> cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new MultiMap<>(false);

            DecodeUtils.decodeCookies(this, header(HeaderNames.HEADER_COOKIE));
        }

        return _cookieMap;
    }

    private MultiMap<String> _cookieMap;


    @Override
    public MultiMap<String> headerMap() {
        if (_headerMap == null) {
            _headerMap = new MultiMap<String>();

            for (String k : _request.getHeaderNames()) {
                _headerMap.holder(k).setValues(new ArrayList<>(_request.getHeaders(k)));
            }
        }

        return _headerMap;
    }

    private MultiMap<String> _headerMap;

    //=================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null && contentType != null) {
            if (contentType.length() > 0 && contentType.indexOf(";") < 0) {
                headerSet(HeaderNames.HEADER_CONTENT_TYPE, contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet(HeaderNames.HEADER_CONTENT_TYPE, contentType);
    }


    private ByteArrayOutputStream _outputStreamTmp;

    @Override
    public OutputStream outputStream() throws IOException {
        sendHeaders(false);

        if (_allows_write) {
            return _response.getOutputStream();
        } else {
            if (_outputStreamTmp == null) {
                _outputStreamTmp = new ByteArrayOutputStream();
            } else {
                _outputStreamTmp.reset();
            }

            return _outputStreamTmp;
        }
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = outputStream();

            if (!_allows_write) {
                return;
            }

            out.write(bytes);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = outputStream();

            if (!_allows_write) {
                return;
            }

            IoUtil.transferTo(stream, out);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void headerSet(String key, String val) {
        //用put才有效
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
        return _status;
    }

    private int _status = 200;

    @Override
    protected void statusDoSet(int status) {
        _status = status;
    }

    @Override
    public void contentLength(long size) {
        _response.setContentLength(size);
    }

    @Override
    public void flush() throws IOException {
        if (_allows_write) {
            outputStream().flush();
        }
    }

    @Override
    public void close() throws IOException {
        _response.close();
    }

    @Override
    protected void innerCommit() throws IOException {
        try {
            if (getHandled() || status() >= 200) {
                sendHeaders(true);
            } else {
                status(404);
                sendHeaders(true);
            }
        } finally {
            _future.complete(null);
        }
    }

    private boolean _headers_sent = false;
    private boolean _allows_write = true;

    private void sendHeaders(boolean isCommit) throws IOException {
        if (!_headers_sent) {
            _headers_sent = true;

            if ("HEAD".equals(method())) {
                _allows_write = false;
            }

            if (sessionState() != null) {
                sessionState().sessionPublish();
            }

            _response.setHttpStatus(HttpStatus.valueOf(status()));

            if (isCommit || _allows_write == false) {
                _response.setContentLength(0);
            }
        }
    }


    ///////////////////////
    // for async
    ///////////////////////

    protected final AsyncContextState asyncState = new AsyncContextState();

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public boolean asyncStarted() {
        return asyncState.isStarted;
    }

    @Override
    public void asyncListener(ContextAsyncListener listener) {
        asyncState.addListener(listener);
    }

    @Override
    public void asyncStart(long timeout, Runnable runnable) {
        if (asyncState.isStarted == false) {
            asyncState.isStarted = true;
            asyncState.asyncDelay(timeout, this, this::innerCommit);

            if (runnable != null) {
                runnable.run();
            }

            asyncState.onStart(this);
        }
    }


    @Override
    public void asyncComplete() {
        if (asyncState.isStarted) {
            try {
                innerCommit();
            } catch (Throwable e) {
                log.warn("Async completion failed", e);
                asyncState.onError(this, e);
            } finally {
                asyncState.onComplete(this);
            }
        }
    }
}
