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
package org.noear.solon.boot.vertx;

import io.netty.buffer.ByteBufInputStream;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.CookieImpl;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.web.*;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.core.util.RunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * @author noear
 * @since 2.9
 */
public class VxWebContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(VxWebContext.class);

    private HttpServerRequest _request;
    private HttpServerResponse _response;
    private Buffer _requestBody;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L;//默认30秒
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected HttpServerRequest innerGetRequest() {
        return _request;
    }

    protected HttpServerResponse innerGetResponse() {
        return _response;
    }

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    protected List<ContextAsyncListener> innerAsyncListeners() {
        return _asyncListeners;
    }

    public VxWebContext(HttpServerRequest request, Buffer requestBody) {
        this._request = request;
        this._requestBody = requestBody;
        this._response = request.response();
    }

    private boolean _loadMultipartFormData = false;

    private void loadMultipartFormData() throws IOException {
        if (_loadMultipartFormData) {
            return;
        } else {
            _loadMultipartFormData = true;
        }

        //文件上传需要
        if (isMultipartFormData()) {
            BodyUtils.decodeMultipart(this, _fileMap);
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        return _request.remoteAddress().host();
    }

    @Override
    public int remotePort() {
        return _request.remoteAddress().port();
    }

    @Override
    public String method() {
        return _request.method().name();
    }

    @Override
    public String protocol() {
        return "http";
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
        return false;
    }

    @Override
    public String url() {
        return _request.absoluteURI();
    }


    private int contentLength = -2;

    @Override
    public long contentLength() {
        if (contentLength > -2) {
            return contentLength;
        } else {
            String tmp = _request.getHeader("Content-Length");
            if (Utils.isEmpty(tmp)) {
                contentLength = -1;
            } else {
                contentLength = Integer.parseInt(tmp);
            }

            return contentLength;
        }
    }

    @Override
    public String queryString() {
        return _request.query();
    }

    private InputStream bodyAsStream;

    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream != null) {
            return bodyAsStream;
        } else {
            if (_requestBody == null) {
                bodyAsStream = new ByteArrayInputStream(new byte[0]);
            } else {
                bodyAsStream = new LimitedInputStream(new ByteBufInputStream(_requestBody.getByteBuf()), ServerProps.request_maxBodySize);
            }

            return bodyAsStream;
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
                //编码窗体预处理
                BodyUtils.decodeFormUrlencoded(this, false);

                //多分段处理
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (Map.Entry<String, String> kv : _request.params()) {
                    _paramMap.add(kv.getKey(), kv.getValue());
                }

                for (Map.Entry<String, String> kv : _request.formAttributes()) {
                    _paramMap.add(kv.getKey(), kv.getValue());
                }
            } catch (Exception e) {
                throw BodyUtils.status4xx(this, e);
            }
        }
    }

    @Override
    public MultiMap<UploadedFile> fileMap() throws IOException {
        if (isMultipartFormData()) {
            loadMultipartFormData();
        }

        return _fileMap;
    }

    @Override
    public MultiMap<String> cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new MultiMap<String>();

            for (Cookie c1 : _request.cookies()) {
                _cookieMap.add(c1.getName(), c1.getValue());
            }
        }

        return _cookieMap;
    }

    private MultiMap<String> _cookieMap;

    @Override
    public MultiMap<String> headerMap() {
        if (_headerMap == null) {
            _headerMap = new MultiMap<String>();

            for (Map.Entry<String, String> kv : _request.headers()) {
                _headerMap.add(kv.getKey(), kv.getValue());
            }
        }

        return _headerMap;
    }

    private MultiMap<String> _headerMap;

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

    private ResponseOutputStream responseOutputStream;

    private ResponseOutputStream responseOutputStream() {
        if (responseOutputStream == null) {
            responseOutputStream = new ResponseOutputStream(_response, 512);
        }

        return responseOutputStream;
    }

    private ByteArrayOutputStream _outputStreamTmp;

    @Override
    public OutputStream outputStream() throws IOException {
        sendHeaders(false);

        if (_allows_write) {
            return responseOutputStream();
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
    public void headerSet(String name, String val) {
        _response.headers().set(name, val);
    }

    @Override
    public void headerAdd(String name, String val) {
        _response.headers().add(name, val);
    }

    @Override
    public String headerOfResponse(String name) {
        return _response.headers().get(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        return _response.headers().getAll(name);
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        return _response.headers().names();
    }

    @Override
    public void cookieSet(String name, String val, String domain, String path, int maxAge) {
        CookieImpl cookie = new CookieImpl(name, val);

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
        if (_headers_sent == false) {
            _response.putHeader("Content-Length", String.valueOf(size));
        }
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
                if (_response.ended() == false) {
                    _response.end();
                }
            }
        }
    }

    @Override
    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            sendHeaders(true);
            flush();
            _response.send();
        } else {
            status(404);
            sendHeaders(true);
            flush();
            _response.send();
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

            _response.setStatusCode(status());

            if (isCommit || _allows_write == false) {
                _response.setChunked(true);
            } else {
                if (_response.headers().contains("Content-Length") == false) {
                    _response.setChunked(true);
                }
            }
        }
    }
}
