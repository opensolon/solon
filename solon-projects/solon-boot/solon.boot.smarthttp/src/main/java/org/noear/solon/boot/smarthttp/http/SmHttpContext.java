/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.boot.smarthttp.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.web.*;
import org.noear.solon.core.NvMap;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.RunUtil;
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

public class SmHttpContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(SmHttpContext.class);

    private HttpRequest _request;
    private HttpResponse _response;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L;//默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected HttpRequest innerGetRequest() {
        return _request;
    }

    protected HttpResponse innerGetResponse() {
        return _response;
    }

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    protected List<ContextAsyncListener> innerAsyncListeners() {
        return _asyncListeners;
    }


    public SmHttpContext(HttpRequest request, HttpResponse response, CompletableFuture<Object> future) {
        _request = request;
        _response = response;
        _asyncFuture = future;

        _filesMap = new HashMap<>();
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
            BodyUtils.decodeMultipart(this, _filesMap);
        }
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
        return _request.getProtocol();
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
            throw BodyUtils.status4xx(this, e);
        }
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
    }

    private NvMap _paramMap;

    @Override
    public NvMap paramMap() {
        paramsMapInit();

        return _paramMap;
    }

    private Map<String, List<String>> _paramsMap;

    @Override
    public Map<String, List<String>> paramsMap() {
        paramsMapInit();

        return _paramsMap;
    }

    private void paramsMapInit() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();
            _paramMap = new NvMap();

            try {
                //编码窗体预处理
                BodyUtils.decodeFormUrlencoded(this);

                //多分段处理
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (Map.Entry<String, String[]> entry : _request.getParameters().entrySet()) {
                    String key = ServerProps.urlDecode(entry.getKey());
                    _paramsMap.put(key, Utils.asList(entry.getValue()));
                    _paramMap.put(key, entry.getValue()[0]);
                }
            } catch (Exception e) {
                throw BodyUtils.status4xx(this, e);
            }
        }
    }

    @Override
    public Map<String, List<UploadedFile>> filesMap() throws IOException {
        if (isMultipartFormData()) {
            loadMultipartFormData();

            return _filesMap;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public NvMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new NvMap();

            for (Cookie c1 : _request.getCookies()) {
                _cookieMap.put(c1.getName(), c1.getValue());
            }
        }

        return _cookieMap;
    }

    private NvMap _cookieMap;


    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new NvMap();

            for (String k : _request.getHeaderNames()) {
                _headerMap.put(k, _request.getHeader(k));
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;

    @Override
    public Map<String, List<String>> headersMap() {
        if (_headersMap == null) {
            _headersMap = new IgnoreCaseMap<>();

            for (String k : _request.getHeaderNames()) {
                _headersMap.put(k, new ArrayList<>(_request.getHeaders(k)));
            }
        }

        return _headersMap;
    }
    private Map<String, List<String>> _headersMap;

    //=================================

    @Override
    public Object response() {
        return _response;
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
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        Cookie cookie = new Cookie(key, val);

        if (Utils.isNotEmpty(path)) {
            cookie.setPath(path);
        }

        if (maxAge >= 0) {
            cookie.setMaxAge(maxAge);
        }

        if (Utils.isNotEmpty(domain)) {
            cookie.setDomain(domain);
        }

        _response.addCookie(cookie);
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
    public void contentLength(long size) {
        _response.setContentLength((int) size);
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
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener, Runnable runnable) {
        if (_isAsync == false) {
            _isAsync = true;

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }

            if (_asyncTimeout > 0) {
                RunUtil.delay(() -> {
                    for (ContextAsyncListener listener1 : _asyncListeners) {
                        try {
                            listener1.onTimeout(this);
                        } catch (IOException e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                }, _asyncTimeout);
            }

            if (runnable != null) {
                runnable.run();
            }
        }
    }


    @Override
    public void asyncComplete() {
        if (_isAsync) {
            try {
                innerCommit();
            } catch (Throwable e) {
                log.warn("Async completion failed", e);
            } finally {
                _asyncFuture.complete(this);
            }
        }
    }

    @Override
    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            sendHeaders(true);
        } else {
            status(404);
            sendHeaders(true);
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
}
