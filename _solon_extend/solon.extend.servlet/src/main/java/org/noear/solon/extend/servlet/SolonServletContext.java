package org.noear.solon.extend.servlet;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

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
 * @author noear
 * @since 1.2
 * */
public class SolonServletContext extends Context {
    private HttpServletRequest _request;
    private HttpServletResponse _response;
    protected Map<String, List<UploadedFile>> _fileMap;

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

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(url());
        }

        return _uri;
    }

    private URI _uri;

    @Override
    public String path() {
        return uri().getPath();
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

    @Override
    public String param(String key, String def) {
        String temp = paramMap().get(key); //因为会添加参数，所以必须用这个

        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return temp;
        }
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
                throw new IllegalStateException(e);
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
    public List<UploadedFile> files(String key) throws Exception {
        if (_fileMap != null && isMultipartFormData()) {
            loadMultipartFormData();

            List<UploadedFile> temp = _fileMap.get(key);
            if (temp == null) {
                return new ArrayList<>();
            } else {
                return temp;
            }
        } else {
            return new ArrayList<>();
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

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                out.write(buff, 0, rc);
            }
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

        if (maxAge > 0) {
            c.setMaxAge(maxAge);
        }

        if (Utils.isNotEmpty(domain)) {
            c.setDomain(domain);
        }

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url) {
        try {
            _response.sendRedirect(url);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void redirect(String url, int code) {
        statusDoSet(code);
        _response.setHeader("Location", url);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    protected void statusDoSet(int status) {
        _response.setStatus(status);
    }

    @Override
    public void flush() throws IOException {
        outputStream().flush();
    }

    @Override
    protected void commit() throws IOException {
        sendHeaders();
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
