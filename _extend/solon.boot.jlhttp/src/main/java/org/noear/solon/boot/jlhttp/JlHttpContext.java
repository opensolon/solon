package org.noear.solon.boot.jlhttp;

import org.noear.solon.core.NvMap;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class JlHttpContext extends Context {
    private HTTPServer.Request _request;
    private HTTPServer.Response _response;
    protected Map<String, List<UploadedFile>> _fileMap;

    public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) {
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
                _ip = _request.getSocket().getInetAddress().getHostAddress();
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
        return _request.getVersion();
    }

    private URI _uri;
    @Override
    public URI uri() {
        if(_uri == null) {
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
            _url = _request.getURI().toString();

            if (_url != null) {
                if (_url.startsWith("/")) {
                    String host = header("Host");

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
        try {
            return _request.getBody().available();
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
        return _request.getURI().getQuery();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getBody();
    }

    @Override
    public String[] paramValues(String key) {
        List<String> list = paramsMap().get(key);
        if (list == null) {
            return null;
        }

        return list.toArray(new String[list.size()]);
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
                _paramMap.putAll(_request.getParams());
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

            try {
                for (String[] kv : _request.getParamsList()) {
                    List<String> list = _paramsMap.get(kv[0]);
                    if (list == null) {
                        list = new ArrayList<>();
                        _paramsMap.put(kv[0], list);
                    }

                    list.add(kv[1]);
                }
            } catch (Exception ex) {
                EventBus.push(ex);
                return null;
            }
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
        sendHeaders();

        if (_allows_write) {
            return _response.getBody();
        }else{
            return new ByteArrayOutputStream();
        }
    }

    @Override
    public void output(byte[] bytes) {
        try {
            OutputStream out = outputStream();
            
            if(!_allows_write){
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

            if(!_allows_write){
                return;
            }

            int len = 0;
            byte[] buf = new byte[512]; //0.5k
            while ((len = stream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    //protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


    @Override
    public void headerSet(String key, String val) {
        _response.getHeaders().replace(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.getHeaders().add(key, val);
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

        _response.getHeaders().add("Set-Cookie", sb.toString());
    }

    @Override
    public void redirect(String url) {
        redirect(url, 302);
    }

    @Override
    public void redirect(String url, int code) {
        try {
            headerSet("Location", url);
            _response.sendHeaders(code);
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
    protected void statusDoSet(int status) {
        _status = status; //jlhttp 的 状态，由 上下文代理 负责
    }

    @Override
    public void flush() throws IOException {
        if (_allows_write) {
            outputStream().flush();
        }
    }


    //jlhttp 需要先输出 header ，但是 header 后面可能会有变化；所以不直接使用  response.getOutputStream()
    @Override
    protected void commit() throws IOException {
        //_response.getOutputStream().close(); //length=-1后，不需要colose()；而且性能大大提搞

        //sendHeaders(); //output时，会自动 sendHeaders

        if (!_response.headersSent()) {
            //
            // 因为header 里没有设内容长度；所有必须要有输出!!
            //
            output("");
        }
    }

    private boolean _allows_write = true;
    private void sendHeaders() throws IOException {
        if (!_response.headersSent()) {
            if("HEAD".equals(method())) {
                _allows_write = false;
            }

            if(sessionState() != null){
                sessionState().sessionPublish();
            }

            //_response.sendHeaders(status()); //不能用这个；
            _response.sendHeaders(status(), -1, -1, null, null, null);
        }
    }
}
