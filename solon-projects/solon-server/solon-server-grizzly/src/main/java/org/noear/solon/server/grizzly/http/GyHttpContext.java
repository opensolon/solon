package org.noear.solon.server.grizzly.http;

import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.handle.AsyncContextState;
import org.noear.solon.server.handle.ContextBase;
import org.noear.solon.server.handle.HeaderNames;
import org.noear.solon.server.io.LimitedInputStream;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.server.util.RedirectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 * @author noear
 * @since 3.6
 */
public class GyHttpContext extends ContextBase {
    static final Logger log = LoggerFactory.getLogger(GyHttpContext.class);

    private final Request _request;
    private final Response _response;

    public GyHttpContext(Request request, Response response) {
        _request = request;
        _response = response;
    }

    protected Request innerGetRequest() {
        return _request;
    }

    protected Response innerGetResponse() {
        return _response;
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
        return _request.getMethod().getMethodString();
    }

    @Override
    public String protocol() {
        return _request.getProtocol().getProtocolString();
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
        return _request.isSecure();
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            _url = _request.getRequestURI();
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
            throw DecodeUtils.status4xx(this, e);
        }
    }

    private LimitedInputStream _inputStream;
    @Override
    public InputStream bodyAsStream() throws IOException {
        if(_inputStream == null) {
            _inputStream = new LimitedInputStream(_request.getInputStream(), ServerProps.request_maxBodySize);
        }

        return _inputStream;
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

                if(_request.getParameters().getEncoding() == null){
                    _request.getParameters().setEncoding(Charset.forName(ServerProps.request_encoding));
                }

                if(_request.getParameters().getQueryStringEncoding() == null){
                    _request.getParameters().setQueryStringEncoding(Charset.forName(ServerProps.request_encoding));
                }

                for (String name : _request.getParameters().getParameterNames()) {
                    String key = ServerProps.urlDecode(name);
                    _paramMap.holder(key).setValues(_request.getParameters().getParameterValues(name));
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
                for (String v : _request.getHeaders(k)) {
                    _headerMap.holder(k).addValue(v);
                }
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
        return Arrays.asList(_response.getHeaderValues(name));
    }


    @Override
    public Collection<String> headerNamesOfResponse() {
        return Arrays.asList(_response.getHeaderNames());
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
        _response.setContentLengthLong(size);
    }

    @Override
    public void flush() throws IOException {
        if (_allows_write) {
            _response.flush();
        }
    }

    @Override
    public void close() throws IOException {
        _response.finish();
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
            _response.finish();
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

            _response.setStatus(status());

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
