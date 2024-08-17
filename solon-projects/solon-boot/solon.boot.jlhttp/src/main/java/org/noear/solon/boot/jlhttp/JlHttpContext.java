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
package org.noear.solon.boot.jlhttp;

import org.noear.jlhttp.HTTPServer;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.io.LimitedInputStream;
import org.noear.solon.boot.web.HeaderUtils;
import org.noear.solon.boot.web.WebContextBase;
import org.noear.solon.boot.web.Constants;
import org.noear.solon.boot.web.RedirectUtils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;
import org.noear.solon.core.util.IoUtil;
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

public class JlHttpContext extends WebContextBase {
    static final Logger log = LoggerFactory.getLogger(JlHttpContext.class);

    private HTTPServer.Request _request;
    private HTTPServer.Response _response;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L;//默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) {
        _request = request;
        _response = response;
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
            MultipartUtil.buildParamsAndFiles(this, _filesMap);
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        return _request.getSocket().getInetAddress().getHostAddress();
    }

    @Override
    public int remotePort() {
        return _request.getSocket().getPort();
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getVersion();
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
            _url = _request.getURI().toString();

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
        return _request.getURI().getQuery();
    }

    private InputStream bodyAsStream ;
    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream == null) {
            bodyAsStream = new LimitedInputStream(_request.getBody(), ServerProps.request_maxBodySize);
        }

        return bodyAsStream;
    }

    @Override
    public String body(String charset) throws IOException {
        try {
            return super.body(charset);
        } catch (Exception e) {
            throw MultipartUtil.status4xx(this, e);
        }
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

    private Map<String, List<String>> paramsMapInit() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();
            _paramMap = new NvMap();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (String[] kv : _request.getParamsList()) {
                    if (!_paramMap.containsKey(kv[0])) {
                        _paramMap.put(kv[0], kv[1]);
                    }

                    List<String> list = _paramsMap.get(kv[0]);
                    if (list == null) {
                        list = new ArrayList<>();
                        _paramsMap.put(kv[0], list);
                    }

                    list.add(kv[1]);
                }
            } catch (Exception e) {
                throw MultipartUtil.status4xx(this, e);
            }
        }

        return _paramsMap;
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
            _cookieMap = new NvMap(_request.getHeaders().getParams("Cookie"));
        }

        return _cookieMap;
    }

    private NvMap _cookieMap;


    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new NvMap();

            HTTPServer.Headers headers = _request.getHeaders();

            if (headers != null) {
                for (HTTPServer.Header h : headers) {
                    _headerMap.put(h.getName(), h.getValue());
                }
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;



    @Override
    public Map<String, List<String>> headersMap() {
        if (_headersMap == null) {
            _headersMap = new IgnoreCaseMap<>();

            HTTPServer.Headers headers = _request.getHeaders();

            if (headers != null) {
                for (HTTPServer.Header h : headers) {
                    List<String> values = _headersMap.get(h.getName());
                    if (values == null) {
                        values = new ArrayList<>();
                        _headersMap.put(h.getName(), values);
                    }

                    values.add(h.getValue());
                }
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
            return _response.getBody();
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
        _response.getHeaders().replace(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.getHeaders().add(key, val);
    }

    @Override
    public String headerOfResponse(String name) {
        return _response.getHeaders().get(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        List<String> values = new ArrayList<>();

        for (HTTPServer.Header h1 : _response.getHeaders()) {
            if (h1.getName().equalsIgnoreCase(name)) {
                values.add(h1.getValue());
            }
        }

        return values;
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        Set<String> names = new HashSet<>();

        for (HTTPServer.Header h1 : _response.getHeaders()) {
            names.add(h1.getName());
        }

        return names;
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
        _status = status; //jlhttp 的 状态，由 上下文代理 负责
    }

    @Override
    public void flush() throws IOException {
        if (_allows_write) {
            outputStream();
            _response.flush();
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

            _asyncFuture = new CompletableFuture<>();

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
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
                log.warn("Async completion failed ", e);
            } finally {
                _asyncFuture.complete(this);
            }
        }
    }

    protected void asyncAwait() throws InterruptedException, ExecutionException, IOException {
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

    //jlhttp 需要先输出 header ，但是 header 后面可能会有变化；所以不直接使用  response.getOutputStream()
    @Override
    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            sendHeaders(true);
        } else {
            if (!_response.headersSent()) {
                _response.sendError(404);
            }
        }
    }

    private boolean _allows_write = true;

    private void sendHeaders(boolean isCommit) throws IOException {
        if (!_response.headersSent()) {
            if ("HEAD".equals(method())) {
                _allows_write = false;
            }

            if (sessionState() != null) {
                sessionState().sessionPublish();
            }

            if (isCommit || _allows_write == false) {
                _response.sendHeaders(status(), 0L, -1, null, null, null);
            } else {
                String tmp = _response.getHeaders().get(Constants.HEADER_CONTENT_LENGTH);

                if (tmp != null) {
                    _response.sendHeaders(status(), Long.parseLong(tmp), -1, null, null, null);
                } else {
                    _response.sendHeaders(status(), -1L, -1, null, null, null);
                }
            }
        }
    }
}
