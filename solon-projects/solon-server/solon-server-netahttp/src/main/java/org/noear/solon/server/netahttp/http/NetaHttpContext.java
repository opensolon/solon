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
package org.noear.solon.server.netahttp.http;

import net.hasor.neta.bytebuf.ByteBuf;
import net.hasor.neta.bytebuf.ByteBufAllocator;
import net.hasor.neta.codec.http.*;
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

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 基于 neta-codec-http 的 Solon Context 实现
 *
 * @author noear
 * @since 3.10
 */
public class NetaHttpContext extends ContextBase {
    static final Logger log = LoggerFactory.getLogger(NetaHttpContext.class);

    /**
     * 手动解析 URL 编码参数（key=value&key2=value2）
     */
    private static void decodeUrlParams(MultiMap<String> paramMap, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            try {
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    paramMap.add(key, val);
                } else if (idx < 0 && pair.length() > 0) {
                    String key = URLDecoder.decode(pair, "UTF-8");
                    paramMap.add(key, "");
                }
            } catch (UnsupportedEncodingException e) {
                // UTF-8 should always be available
            }
        }
    }

    private final FullHttpRequest request;
    private FullHttpResponse response;
    private boolean responseCommitted = false;

    public NetaHttpContext(FullHttpRequest request) {
        this.request = request;
    }

    /**
     * 获取原始请求对象
     */
    public FullHttpRequest innerGetRequest() {
        return request;
    }

    /**
     * 获取/创建响应对象
     */
    public FullHttpResponse innerGetResponse() {
        if (response == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.OK);
        }
        return response;
    }

    @Override
    public boolean isHeadersSent() {
        return responseCommitted;
    }

    @Override
    public Object request() {
        return request;
    }

    @Override
    public String remoteIp() {
        return "0.0.0.0"; // neta 不直接暴露 remote address，在 handler 层可设置
    }

    @Override
    public int remotePort() {
        return 0;
    }

    @Override
    public int localPort() {
        return 0;
    }

    @Override
    public String method() {
        return request.method().name();
    }

    @Override
    public String protocol() {
        return request.protocolVersion().text();
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
        return false; // 由外部配置决定
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            _url = request.uri();
        }
        return _url;
    }

    @Override
    public long contentLength() {
        String lenStr = request.getString("Content-Length");
        if (lenStr != null) {
            try {
                return Long.parseLong(lenStr);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        ByteBuf content = request.content();
        return content != null ? content.readableBytes() : 0;
    }

    private String queryString;

    @Override
    public String queryString() {
        if (queryString == null) {
            String uri = request.uri();
            int idx = uri.indexOf('?');
            if (idx < 0) {
                queryString = "";
            } else {
                queryString = uri.substring(idx + 1);
            }
        }
        return queryString;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        ByteBuf content = request.content();
        if (content == null || content.readableBytes() == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        return new ByteArrayInputStream(bytes);
    }

    private MultiMap<String> _paramMap;

    @Override
    public MultiMap<String> paramMap() {
        if (_paramMap == null) {
            _paramMap = new MultiMap<>();
            try {
                String qs = queryString();
                if (qs != null && qs.length() > 0) {
                    decodeUrlParams(_paramMap, qs);
                }

                // 如果是 form 表单
                String ct = contentType();
                if (ct != null && ct.contains("application/x-www-form-urlencoded")) {
                    ByteBuf content = request.content();
                    if (content != null && content.readableBytes() > 0) {
                        byte[] bytes = new byte[content.readableBytes()];
                        content.readBytes(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        decodeUrlParams(_paramMap, body);
                    }
                }
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        return _paramMap;
    }

    @Override
    public MultiMap<UploadedFile> fileMap() {
        // TODO: 可后续基于 multipart 解析实现
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
            _headerMap = new MultiMap<>();
            for (String name : request.headerNames()) {
                List<String> values = request.getValues(name);
                if (values != null) {
                    _headerMap.holder(name).setValues(new ArrayList<>(values));
                }
            }
        }
        return _headerMap;
    }

    private MultiMap<String> _headerMap;

    // ========== Response 相关 ==========

    @Override
    public Object response() {
        return innerGetResponse();
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
            if (_outputStream == null) {
                _outputStream = new ByteArrayOutputStream();
            }
            return _outputStream;
        } else {
            if (_outputStreamTmp == null) {
                _outputStreamTmp = new ByteArrayOutputStream();
            } else {
                _outputStreamTmp.reset();
            }
            return _outputStreamTmp;
        }
    }

    private ByteArrayOutputStream _outputStream;

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
        innerGetResponse().setHeader(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        innerGetResponse().addHeader(key, val);
    }

    @Override
    public String headerOfResponse(String name) {
        return innerGetResponse().getString(name);
    }

    @Override
    public Collection<String> headerValuesOfResponse(String name) {
        List<String> vals = innerGetResponse().getValues(name);
        return vals != null ? vals : Collections.emptyList();
    }

    @Override
    public Collection<String> headerNamesOfResponse() {
        return innerGetResponse().headerNames();
    }

    @Override
    public void cookieSet(org.noear.solon.core.handle.Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.name).append("=").append(cookie.value);
        if (cookie.maxAge >= 0) {
            sb.append("; Max-Age=").append(cookie.maxAge);
        }
        if (Utils.isNotEmpty(cookie.domain)) {
            sb.append("; Domain=").append(cookie.domain);
        }
        if (Utils.isNotEmpty(cookie.path)) {
            sb.append("; Path=").append(cookie.path);
        }
        if (cookie.secure) {
            sb.append("; Secure");
        }
        if (cookie.httpOnly) {
            sb.append("; HttpOnly");
        }
        headerAdd(HeaderNames.HEADER_SET_COOKIE, sb.toString());
    }

    @Override
    public void redirect(String url, int code) {
        url = RedirectUtils.getRedirectPath(url);
        headerSet(HeaderNames.HEADER_LOCATION, url);
        statusDoSet(code);
    }

    private int _status = 200;

    @Override
    public int status() {
        return _status;
    }

    @Override
    protected void statusDoSet(int status) {
        _status = status;
    }

    @Override
    public void contentLength(long size) {
        headerSet(HeaderNames.HEADER_CONTENT_LENGTH, String.valueOf(size));
    }

    @Override
    public void flush() throws IOException {
        // neta 是非流式响应，flush 无需操作
    }

    @Override
    public void close() throws IOException {
        // nothing
    }

    private boolean _headers_sent = false;
    private boolean _allows_write = true;

    private void sendHeaders(boolean isCommit) {
        if (!_headers_sent) {
            _headers_sent = true;
            responseCommitted = true;

            if ("HEAD".equals(method())) {
                _allows_write = false;
            }

            try {
                if (sessionState() != null) {
                    sessionState().sessionPublish();
                }
            } catch (Throwable e) {
                // Solon may not be initialized in standalone mode
            }

            innerGetResponse().status(HttpStatus.valueOf(_status));

            if (isCommit || !_allows_write) {
                headerSet(HeaderNames.HEADER_CONTENT_LENGTH, "0");
            }
        }
    }

    /**
     * 提交响应，生成 FullHttpResponse
     */
    public FullHttpResponse commitResponse() {
        sendHeaders(true);

        FullHttpResponse oldResp = innerGetResponse();

        // 写入 body 内容
        byte[] bodyBytes = (_outputStream != null && _outputStream.size() > 0 && _allows_write)
                ? _outputStream.toByteArray() : new byte[0];

        ByteBuf contentBuf = ByteBufAllocator.DEFAULT.heapBuffer(bodyBytes.length);
        if (bodyBytes.length > 0) {
            contentBuf.writeBytes(bodyBytes);
        }

        // 重新构造 response，确保 content 正确绑定
        FullHttpResponse resp = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpStatus.valueOf(_status), contentBuf);

        // 复制 headers
        for (String name : oldResp.headerNames()) {
            List<String> values = oldResp.getValues(name);
            if (values != null) {
                for (String val : values) {
                    resp.addHeader(name, val);
                }
            }
        }

        // 确保 Content-Length 正确
        String clKey = HeaderNames.HEADER_CONTENT_LENGTH;
        resp.setHeader(clKey, String.valueOf(bodyBytes.length));

        return resp;
    }

    @Override
    protected void innerCommit() throws IOException {
        // 响应在 handler 外统一通过 commitResponse() 提交
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
        if (!asyncState.isStarted) {
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
