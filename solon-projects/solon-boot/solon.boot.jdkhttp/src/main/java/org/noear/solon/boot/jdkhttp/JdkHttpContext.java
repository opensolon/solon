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
package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.web.*;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.NvMap;
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
import java.util.concurrent.TimeUnit;

public class JdkHttpContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(JdkHttpContext.class);

    private HttpExchange _exchange;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L; //默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected boolean innerIsAsync() {
        return _isAsync;
    }


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
            BodyUtils.decodeMultipart(this, _filesMap);
        }
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

    @Override
    public long contentLength() {
        return HeaderUtils.getContentLengthLong(this);
    }

    @Override
    public String queryString() {
        return _exchange.getRequestURI().getQuery();
    }


    private InputStream bodyAsStream ;
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
            throw BodyUtils.status4xx(this, e);
        }
    }

    private NvMap _paramMap;

    @Override
    public NvMap paramMap() {
        paramsMapInit();

        return _paramMap;
    }

    /**
     * @since 2.7
     * @since 2.9
     */
    private void paramsMapInit() {
        if (_paramMap == null) {
            _paramMap = new NvMap();

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
                throw BodyUtils.status4xx(this, e);
            }
        }
    }

    @Override
    public MultiMap<UploadedFile> fileMap() throws IOException {
        if (isMultipartFormData()) {
            loadMultipartFormData();
        }

        return _filesMap;
    }

    @Override
    public NvMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new NvMap();

            String tmp = headerOrDefault(Constants.HEADER_COOKIE, "");
            String[] ss = tmp.split(";");
            for (String s : ss) {
                String[] kv = s.split("=");
                if (kv.length > 1) {
                    _cookieMap.add(kv[0].trim(), kv[1].trim());
                } else {
                    _cookieMap.add(kv[0].trim(), "");
                }
            }
        }

        return _cookieMap;
    }

    private NvMap _cookieMap;

    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new NvMap();

            Headers headers = _exchange.getRequestHeaders();

            if (headers != null) {
                for (Map.Entry<String, List<String>> kv : headers.entrySet()) {
                    _headerMap.holder(kv.getKey()).setValues(kv.getValue());
                }
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;

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
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(val).append(";");

        if (Utils.isNotEmpty(path)) {
            sb.append("path=").append(path).append(";");
        }

        if (maxAge >= 0) {
            sb.append("max-age=").append(maxAge).append(";");
        }

        if (Utils.isNotEmpty(domain)) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        headerAdd(Constants.HEADER_SET_COOKIE, sb.toString());
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
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener, Runnable runnable) {
        if (_isAsync == false) {
            _isAsync = true;

            _asyncFuture = new CompletableFuture<>();

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }

            if(runnable != null) {
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

    protected void asyncAwait() throws InterruptedException, ExecutionException, IOException{
        if(_isAsync){
            if (_asyncTimeout > 0) {
                try {
                    _asyncFuture.get(_asyncTimeout, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    for (ContextAsyncListener listener1 : _asyncListeners) {
                        listener1.onTimeout(this);
                    }
                }
            } else {
                _asyncFuture.get();
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
}
