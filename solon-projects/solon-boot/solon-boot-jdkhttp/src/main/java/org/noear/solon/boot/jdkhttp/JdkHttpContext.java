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
package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.web.*;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.Cookie;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JdkHttpContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(JdkHttpContext.class);

    private HttpExchange _exchange;

    public JdkHttpContext(HttpExchange exchange) {
        _exchange = exchange;
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
            DecodeUtils.decodeMultipart(this, _fileMap);
        }
    }

    @Override
    public boolean isHeadersSent() {
        return _headers_sent;
    }

    @Override
    public Object request() {
        return _exchange;
    }


    @Override
    public String remoteIp() {
        return _exchange.getRemoteAddress().getAddress().getHostAddress();
    }

    @Override
    public int remotePort() {
        return _exchange.getRemoteAddress().getPort();
    }

    @Override
    public String method() {
        return _exchange.getRequestMethod();
    }

    @Override
    public String protocol() {
        return _exchange.getProtocol();
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
        return "https".equals(uri().getScheme());
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            _url = _exchange.getRequestURI().toString();

            if (_url != null) {
                if (_url.startsWith("/")) {
                    String host = header(Constants.HEADER_HOST);

                    if (host == null) {
                        host = header(":authority");
                        String scheme = header(":scheme");

                        if (host == null) {
                            host = "localhost";
                        }

                        if (scheme != null) {
                            _url = "https://" + host + _url;
                        } else {
                            _url = scheme + "://" + host + _url;
                        }

                    } else {
                        _url = "http://" + host + _url;
                    }
                }

                int idx = _url.indexOf("?");
                if (idx > 0) {
                    _url = _url.substring(0, idx);
                }
            }
        }

        return _url;
    }

    private long contentLength = -2;

    @Override
    public long contentLength() {
        if (contentLength < -1) {
            contentLength = DecodeUtils.decodeContentLengthLong(this);
        }

        return contentLength;
    }

    @Override
    public String queryString() {
        return _exchange.getRequestURI().getQuery();
    }


    private InputStream bodyAsStream;

    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream == null) {
            bodyAsStream = new LimitedInputStream(_exchange.getRequestBody(), ServerProps.request_maxBodySize);
        }

        return bodyAsStream;
    }

    @Override
    public String body(String charset) throws IOException {
        try {
            return super.body(charset);
        } catch (Exception e) {
            throw DecodeUtils.status4xx(this, e);
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
            _paramMap = new MultiMap<>();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                Map<String, Object> _parameters = ParameterUtil.doFilter(_exchange);

                for (Map.Entry<String, Object> kv : _parameters.entrySet()) {
                    String k = kv.getKey(); //内部已 urlDecode
                    Object v = kv.getValue();

                    if (v instanceof List) {
                        _paramMap.holder(k).setValues((List<String>) v);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add((String) v);

                        _paramMap.holder(k).setValues(list);
                    }
                }
            } catch (Exception e) {
                throw DecodeUtils.status4xx(this, e);
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
            _cookieMap = new MultiMap<>();

            DecodeUtils.decodeCookies(this, header(Constants.HEADER_COOKIE));
        }

        return _cookieMap;
    }

    private MultiMap<String> _cookieMap;

    @Override
    public MultiMap<String> headerMap() {
        if (_headerMap == null) {
            _headerMap = new MultiMap<>();

            Headers headers = _exchange.getRequestHeaders();

            if (headers != null) {
                for (Map.Entry<String, List<String>> kv : headers.entrySet()) {
                    _headerMap.holder(kv.getKey()).setValues(kv.getValue());
                }
            }
        }

        return _headerMap;
    }

    private MultiMap<String> _headerMap;

    @Override
    public Object response() {
        return _exchange;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet(Constants.HEADER_CONTENT_TYPE, contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet(Constants.HEADER_CONTENT_TYPE, contentType);
    }


    private ByteArrayOutputStream _outputStreamTmp;

    @Override
    public OutputStream outputStream() throws IOException {
        sendHeaders(false);

        if (_allows_write) {
            return _exchange.getResponseBody();
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
        _exchange.getResponseHeaders().set(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _exchange.getResponseHeaders().add(key, val);
    }


    @Override
    public String headerOfResponse(String name) {
        return _exchange.getResponseHeaders().getFirst(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        return _exchange.getResponseHeaders().get(name);
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        return _exchange.getResponseHeaders().keySet();
    }

    @Override
    public void cookieSet(Cookie cookie) {
        StringBuilder buf = new StringBuilder();
        buf.append(cookie.name).append("=").append(cookie.value).append(";");

        if (cookie.maxAge >= 0) {
            buf.append("max-age=").append(cookie.maxAge).append(";");
        }

        if (Utils.isNotEmpty(cookie.domain)) {
            buf.append("domain=").append(cookie.domain.toLowerCase()).append(";");
        }

        if (Utils.isNotEmpty(cookie.path)) {
            buf.append("path=").append(cookie.path).append(";");
        }

        if (cookie.secure) {
            buf.append("secure").append(";");
        }

        if (cookie.httpOnly) {
            buf.append("httponly").append(";");
        }

        headerAdd(Constants.HEADER_SET_COOKIE, buf.toString());
    }

    @Override
    public void redirect(String url, int code) {
        url = RedirectUtils.getRedirectPath(url);

        headerSet(Constants.HEADER_LOCATION, url);
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
    public void flush() throws IOException {
        if (_allows_write) {
            outputStream().flush();
        }
    }

    @Override
    public void close() throws IOException {
        _exchange.close();
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
            if (asyncState.asyncFuture != null) {
                asyncState.asyncFuture.complete(null);
            }
        }
    }

    private boolean _allows_write = true;
    private boolean _headers_sent = false;

    private void sendHeaders(boolean isCommit) throws IOException {
        if (!_headers_sent) {
            _headers_sent = true;

            if ("HEAD".equals(method())) {
                _allows_write = false;
            }

            if (sessionState() != null) {
                sessionState().sessionPublish();
            }

            if (isCommit || _allows_write == false) {
                _exchange.sendResponseHeaders(status(), -1L);
            } else {
                List<String> tmp = _exchange.getResponseHeaders().get(Constants.HEADER_CONTENT_LENGTH);

                if (tmp != null && tmp.size() > 0) {
                    _exchange.sendResponseHeaders(status(), Long.parseLong(tmp.get(0)));
                } else {
                    _exchange.sendResponseHeaders(status(), 0L);
                }
            }
        }
    }

    ///////////////////////
    // for async
    ///////////////////////

    private AsyncContextState asyncState = new AsyncContextState();


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

            asyncState.asyncFuture = new CompletableFuture<>();
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

    protected void asyncAwait() throws InterruptedException, ExecutionException {
        if (asyncState.isStarted) {
            asyncState.asyncFuture.get();
        }
    }
}