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
package org.noear.solon.server.nettyhttp;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.noear.solon.Utils;
import org.noear.solon.server.handle.HeaderNames;
import org.noear.solon.server.util.HeaderUtils;
import org.noear.solon.server.util.RedirectUtils;
import org.noear.solon.server.handle.ContextBase;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;
import org.noear.solon.core.util.IoUtil;

public class NettyHttpContext extends ContextBase {

    private final ChannelHandlerContext ctx;
    private FullHttpRequest request;
    private FullHttpResponse response;
    protected Map<String, List<UploadedFile>> _fileMap;

    private boolean _isAsync;
    private long _asyncTimeout = 30000L; //默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected boolean innerIsAsync() {
        return _isAsync;
    }


    public NettyHttpContext(final ChannelHandlerContext ctx,
            final FullHttpRequest request,
            final FullHttpResponse response) {
        this.ctx = ctx;
        this.request = request;
        this.response = response;
        _fileMap = new HashMap<>();
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
            MultipartUtil.buildParamsAndFiles(this);
        }
    }

    @Override
    public Object request() {
        return request;
    }


    @Override
    public String remoteIp() {
        return null;
    }

    @Override
    public int remotePort() {
        return 0;
    }

    @Override
    public String method() {
        return request.method().name();
    }

    @Override
    public String protocol() {
        return request.protocolVersion().protocolName();
    }

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = this.createURI(url());
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
            _url = request.uri();

            if (_url != null) {
                if (_url.startsWith("/")) {
                    String host = header(HeaderNames.HEADER_HOST);

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
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        String query = queryStringDecoder.parameters().get("query").get(0);
        return query;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return new ByteBufInputStream(request.content());
    }

    private NvMap _paramMap;

    @Override
    public NvMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new NvMap();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }


            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return _paramMap;
    }

    private Map<String, List<String>> _paramsMap;

    @Override
    public Map<String, List<String>> paramsMap() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

        }

        return _paramsMap;
    }

    @Override
    public Map<String, List<UploadedFile>> filesMap() throws IOException {
        if (isMultipartFormData()) {
            loadMultipartFormData();

            return _fileMap;
        } else {
            return Collections.emptyMap();
        }
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
                    _cookieMap.put(kv[0].trim(), kv[1].trim());
                } else {
                    _cookieMap.put(kv[0].trim(), null);
                }
            }
        }

        return _cookieMap;
    }

    private NvMap _cookieMap;

    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            resolveHeaders();
        }

        return _headerMap;
    }

    private NvMap _headerMap;

    private void resolveHeaders() {
        _headerMap = new NvMap();
        _headersMap = new IgnoreCaseMap<>();

        HttpHeaders headers = request.headers();
        if (headers != null) {
            headers.forEach(kv -> {
                _headersMap.put(kv.getKey(), Collections.singletonList(kv.getValue()));
            });
        }
    }

    @Override
    public Map<String, List<String>> headersMap() {
        if (_headersMap == null) {
            resolveHeaders();
        }
        return _headersMap;
    }

    private Map<String, List<String>> _headersMap;

    @Override
    public Object response() {
        return response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null && contentType != null) {
            if (contentType.length() > 0 && contentType.indexOf(";") < 0) {
                headerSet(Constants.HEADER_CONTENT_TYPE, contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet(Constants.HEADER_CONTENT_TYPE, contentType);
    }


    private ByteArrayOutputStream _outputStreamTmp;

    @Override
    public OutputStream outputStream() throws IOException {
        //        sendHeaders(false);

        if (_allows_write) {
            return new ChannelOutputStream(ctx.channel(), response);
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
        response.content().writeBytes(bytes);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
        //        try {
        //            OutputStream out = outputStream();
        //
        //            if (!_allows_write) {
        //                return;
        //            }
        //
        //            out.write(bytes);
        //        } catch (Throwable ex) {
        //            throw new RuntimeException(ex);
        //        }
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
        response.headers().set(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        response.headers().add(key, val);
    }


    @Override
    public String headerOfResponse(String name) {
        return response.headers().get(name);
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

    private int _status = HttpResponseStatus.OK.code();

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
        ctx.close();
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener) {
        if (_isAsync == false) {
            _isAsync = true;

            _asyncFuture = new CompletableFuture<>();

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }
        }
    }


    @Override
    public void asyncComplete() throws IOException {
        if (_isAsync) {
            try {
                innerCommit();
            } finally {
                _asyncFuture.complete(this);
            }
        }
    }

    protected void asyncAwait() throws InterruptedException, ExecutionException, IOException {
        if (_isAsync) {
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

            response.setStatus(HttpResponseStatus.valueOf(status()));

            if (isCommit || _allows_write == false) {
                sendOk();
            } else {
                String tmp = response.headers().get(Constants.HEADER_CONTENT_LENGTH);
                if (tmp != null) {
                    sendOk(Long.parseLong(tmp));
                } else {
                    sendOk();
                }
            }
        }
    }

    public void sendError() {
        response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        response.headers().set(Constants.HEADER_CONTENT_LENGTH, "0");
        ctx.writeAndFlush(response);
    }

    public void send(final byte[] bytes) {
        response.content().writeBytes(bytes);
        response.headers().set(Constants.HEADER_CONTENT_LENGTH, bytes.length);
        ctx.writeAndFlush(response);
    }

    public void sendOk(final long length) {
        response.headers().set(Constants.HEADER_CONTENT_LENGTH, length);
        ctx.writeAndFlush(response);
    }

    public void sendOk() {
        sendOk(0);
    }
}
