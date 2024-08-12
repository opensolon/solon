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
package org.noear.solon.web.servlet;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.web.Constants;
import org.noear.solon.boot.web.WebContextBase;
import org.noear.solon.boot.web.RedirectUtils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;
import org.noear.solon.core.util.IoUtil;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * */
public class SolonServletContext extends WebContextBase {
    private HttpServletRequest _request;
    private HttpServletResponse _response;

    protected boolean innerIsAsync() {
        return asyncContext != null;
    }

    public SolonServletContext(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        _filesMap = new HashMap<>();

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
            MultipartUtil.buildParamsAndFiles(this, _filesMap);
        }
    }

    @Override
    public Object pull(Class<?> clz) {
        Object tmp = super.pull(clz);

        if (tmp == null) {
            if (clz.isInstance(_request.getSession())) {
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

    private Map<String, List<String>> paramsMapInit() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();
            _paramMap = new NvMap();

            try {
                if (autoMultipart()) {
                    loadMultipartFormData();
                }

                for (Map.Entry<String, String[]> kv : _request.getParameterMap().entrySet()) {
                    String name = ServerProps.urlDecode(kv.getKey());

                    _paramsMap.put(name, Utils.asList(kv.getValue()));
                    _paramMap.put(name, kv.getValue()[0]);
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

    private NvMap _cookieMap;

    @Override
    public NvMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new NvMap();

            Cookie[] _cookies = _request.getCookies();

            if (_cookies != null) {
                for (Cookie c : _cookies) {
                    _cookieMap.put(c.getName(), c.getValue());
                }
            }
        }

        return _cookieMap;
    }

    @Override
    public NvMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new NvMap();
            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                String value = _request.getHeader(key);
                _headerMap.put(key, value);
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;


    @Override
    public Map<String, List<String>> headersMap() {
        if (_headersMap == null) {
            _headersMap = new IgnoreCaseMap<>();

            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                _headersMap.put(key, Collections.list(_request.getHeaders(key)));
            }
        }
        return _headersMap;
    }
    private Map<String, List<String>> _headersMap;


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
    public Collection<String> headerNamesOfResponse(){
        return _response.getHeaderNames();
    }


    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        Cookie c = new Cookie(key, val);

        if (Utils.isNotEmpty(path)) {
            c.setPath(path);
        }

        if (maxAge >= 0) {
            c.setMaxAge(maxAge);
        }

        if (Utils.isNotEmpty(domain)) {
            c.setDomain(domain);
        }

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url, int code) {
        url = RedirectUtils.getRedirectPath(url);

        headerSet(Constants.HEADER_LOCATION, url);
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

    AsyncContext asyncContext;

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener) {
        if (asyncContext == null) {
            asyncContext = _request.startAsync();

            if (listener != null) {
                asyncContext.addListener(new AsyncListenerWrap(this, listener));
            }

            if (timeout != 0) {
                //内部默认30秒
                asyncContext.setTimeout(timeout);
            }
        }
    }

    @Override
    public void asyncComplete() throws IOException {
        if (asyncContext != null) {
            try {
                innerCommit();
            } finally {
                asyncContext.complete();
            }
        }
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
}