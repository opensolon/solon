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
package org.noear.solon.web.vertx;

import io.netty.buffer.ByteBufInputStream;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.CookieImpl;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerProps;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.Cookie;
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

import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * @author noear
 * @since 2.9
 */
public class VxWebContext extends ContextBase {
    static final Logger log = LoggerFactory.getLogger(VxWebContext.class);

    private final Vertx vertx;
    private final HttpServerRequest _request;
    private final HttpServerResponse _response;
    private final Buffer _requestBody;

    protected HttpServerRequest innerGetRequest() {
        return _request;
    }

    protected HttpServerResponse innerGetResponse() {
        return _response;
    }

    public VxWebContext(Vertx vertx, HttpServerRequest request, Buffer requestBody) {
        this.vertx = vertx;
        this._request = request;
        this._requestBody = requestBody;
        this._response = request.response();
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
            if (_requestBody != null) {
                DecodeUtils.decodeMultipart(this, new ByteBufInputStream(_requestBody.getByteBuf()), _fileMap);
            }
        }
    }

    public Vertx getVertx() {
        return vertx;
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
        return _request.remoteAddress().host();
    }

    @Override
    public int remotePort() {
        return _request.remoteAddress().port();
    }

    @Override
    public int localPort() {
        return _request.localAddress().port();
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
            _uri = this.parseURI(url());
        }

        return _uri;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            String tmp = _request.absoluteURI();
            int idx = tmp.indexOf('?');
            if (idx < 0) {
                _url = tmp;
            } else {
                _url = tmp.substring(0, idx);
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
                bodyAsStream = new RequestInputStream(_requestBody.getByteBuf(), ServerProps.request_maxBodySize);
            }

            return bodyAsStream;
        }
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
            _paramMap = new MultiMap<String>();

            try {
                //编码窗体预处理
                DecodeUtils.decodeFormUrlencoded(this, false);

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
            _cookieMap = new MultiMap<>(false);

            //_request.cookies() 可能不支持多个同名 cookie
            DecodeUtils.decodeCookies(this, header(HeaderNames.HEADER_COOKIE));
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
        if (charset != null && contentType != null) {
            if (contentType.length() > 0 && contentType.indexOf(";") < 0) {
                headerSet(HeaderNames.HEADER_CONTENT_TYPE, contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet(HeaderNames.HEADER_CONTENT_TYPE, contentType);
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
    public void cookieSet(Cookie cookie) {
        CookieImpl c = new CookieImpl(cookie.name, cookie.value);

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
    protected void innerCommit() throws IOException {
        try {
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
        } finally {
            if (_response.ended() == false) {
                _response.end();
            }
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
