package org.noear.solon.boot.smarthttp.http;

import org.noear.solon.core.NvMap;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.smartboot.http.common.Cookie;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class SmartHttpContext extends Context {
    private HttpRequest _request;
    private HttpResponse _response;
    protected Map<String, List<UploadedFile>> _fileMap;

    public SmartHttpContext(HttpRequest request, HttpResponse response) {
        _request = request;
        _response = response;

        //文件上传需要
        if (isMultipart()) {
            try {
                _fileMap = new HashMap<>();
                MultipartUtil.buildParamsAndFiles(this);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
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

    @Override
    public String path() {
        return uri().getPath();
    }

    private String _url;

    @Override
    public String url() {
        if (_url == null) {
            _url = _request.getRequestURL();
        }

        return _url;
    }

    @Override
    public long contentLength() {
        try {
            return _request.getContentLength();
        } catch (Exception ex) {
            EventBus.push(ex);
            return 0;
        }
    }

    @Override
    public String contentType() {
        return header("Content-Type");
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
        try {
            String temp = paramMap().get(key);

            if (Utils.isEmpty(temp)) {
                return def;
            } else {
                return temp;
            }
        } catch (Exception ex) {
            EventBus.push(ex);

            return def;
        }
    }

    private NvMap _paramMap;

    @Override
    public NvMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new NvMap();

            try {
                for (Map.Entry<String, String[]> entry : _request.getParameters().entrySet()) {
                    _paramMap.put(entry.getKey(), entry.getValue()[0]);
                }
            } catch (Exception ex) {
                EventBus.push(ex);
            }
        }

        return _paramMap;
    }

    private Map<String, List<String>> _paramsMap;

    @Override
    public Map<String, List<String>> paramsMap() {
        if (_paramsMap == null) {
            _paramsMap = new LinkedHashMap<>();

            _request.getParameters().forEach((k, v) -> {
                _paramsMap.put(k, Arrays.asList(v));
            });
        }

        return _paramsMap;
    }


    @Override
    public List<UploadedFile> files(String key) throws Exception {
        if (isMultipartFormData()) {
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

    @Override
    public NvMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new NvMap();

            String _cookieMapStr = header("Cookie");
            if (_cookieMapStr != null) {
                String[] cookies = _cookieMapStr.split(";");

                for (String c1 : cookies) {
                    String[] ss = c1.trim().split("=");
                    if (ss.length == 2) {
                        _cookieMap.put(ss[0].trim(), ss[1].trim());
                    }

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

            for (String k : _request.getHeaderNames()) {
                _headerMap.put(k, _request.getHeader(k));
            }
        }

        return _headerMap;
    }

    private NvMap _headerMap;

    //=================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    protected void contentTypeDoSet(String contentType) {
        if (charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet("Content-Type", contentType + ";charset=" + charset);
                return;
            }
        }

        headerSet("Content-Type", contentType);
    }


    @Override
    public OutputStream outputStream() throws IOException {
        return _outputStream;
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = _outputStream;

            out.write(bytes);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = _outputStream;

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                out.write(buff, 0, rc);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    protected ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();


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
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        Cookie cookie = new Cookie(key, val);

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
    public void redirect(String url) {
        redirect(url, 302);
    }

    @Override
    public void redirect(String url, int code) {
        try {
            headerSet("Location", url);
            statusSet(code);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int status() {
        return _status;
    }

    private int _status = 200;

    @Override
    public void statusSet(int status) {
        _status = status;
        //_response.setHttpStatus(HttpStatus.valueOf(status));
    }

    @Override
    public void flush() throws IOException {
        //不用实现
    }

    @Override
    protected void commit() throws IOException {
        _response.setHttpStatus(HttpStatus.valueOf(status()));

        sendHeaders();

        if ("HEAD".equals(method())) {
            _response.setContentLength(0);
        } else {
            OutputStream out = _response.getOutputStream();
            _response.setContentLength(_outputStream.size());
            _outputStream.writeTo(out);
            out.close();
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
