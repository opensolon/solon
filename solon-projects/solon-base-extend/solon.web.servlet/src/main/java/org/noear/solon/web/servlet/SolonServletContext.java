package org.noear.solon.web.servlet;

import org.noear.solon.Utils;
import org.noear.solon.boot.web.Constants;
import org.noear.solon.boot.web.ContextBase;
import org.noear.solon.boot.web.RedirectUtils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.IgnoreCaseMap;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * SServlet，适配为 Context
 * @author noear
 * @since 1.2
 * */
public class SolonServletContext extends ContextBase {
    private HttpServletRequest _request;
    private HttpServletResponse _response;
    protected Map<String, List<UploadedFile>> _fileMap;

    protected boolean innerIsAsync() {
        return asyncContext != null;
    }

    public SolonServletContext(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        _fileMap = new HashMap<>();

        if (sessionState().replaceable()) {
            sessionState = new SolonServletSessionState(request);
        }
    }

    private boolean _loadMultipartFormData = false;
    private void loadMultipartFormData() throws IOException, ServletException {
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
        return _request;
    }

    private String _ip;

    @Override
    public String ip() {
        if (_ip == null) {
            _ip = header("X-Forwarded-For");

            if (_ip == null) {
                _ip = _request.getRemoteAddr();
            }
        }

        return _ip;
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
        return _request.getContentLength();
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
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
    }

    @Override
    public String[] paramValues(String key) {
        return _request.getParameterValues(key);
    }

    @Override
    public String param(String key) {
        //要充许为字符串
        //默认不能为null
        return paramMap().get(key);
    }

    private NvMap _paramMap;

    @Override
    public NvMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new NvMap();

            try {
                if(autoMultipart()) {
                    loadMultipartFormData();
                }

                Enumeration<String> names = _request.getParameterNames();

                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    String value = _request.getParameter(name);
                    _paramMap.put(name, value);
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

            _request.getParameterMap().forEach((k, v) -> {
                _paramsMap.put(k, Arrays.asList(v));
            });
        }

        return _paramsMap;
    }

    @Override
    public Map<String, List<UploadedFile>> filesMap() throws IOException {
        if (isMultipartFormData()) {
            try {
                loadMultipartFormData();
            } catch (ServletException e) {
                throw new IOException(e);
            }

            return _fileMap;
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

            Utils.transferTo(stream, out);
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
