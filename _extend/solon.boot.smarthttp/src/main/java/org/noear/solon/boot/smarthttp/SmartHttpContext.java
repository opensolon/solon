package org.noear.solon.boot.smarthttp;

import org.noear.solon.core.XMap;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.smartboot.http.HttpRequest;
import org.smartboot.http.HttpResponse;
import org.smartboot.http.enums.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class SmartHttpContext extends XContext {
    private HttpRequest _request;
    private HttpResponse _response;
    protected Map<String,List<XFile>> _fileMap;

    public SmartHttpContext(HttpRequest request, HttpResponse response) {
        _request = request;
        _response = response;

        //文件上传需要
        if (isMultipart()) {
            try {
                _fileMap = new HashMap<>();
                MultipartUtil.buildParamsAndFiles(this);
            }catch (Throwable ex){
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
        if(_ip == null) {
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
        if(_uri == null){
            _uri =URI.create(url());
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
        }catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

    @Override
    public String contentType() {
        return header("Content-Type");
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

            if(XUtil.isEmpty(temp)){
                return def;
            }else{
                return temp;
            }
        }catch (Exception ex){
            ex.printStackTrace();

            return def;
        }
    }

    private XMap _paramMap;
    @Override
    public XMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new XMap();

            try {
                for(Map.Entry<String,String[]> entry:_request.getParameters().entrySet()){
                    _paramMap.put(entry.getKey(),entry.getValue()[0]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
    public List<XFile> files(String key) throws Exception {
        if (isMultipartFormData()){
            List<XFile> temp = _fileMap.get(key);
            if(temp == null){
                return new ArrayList<>();
            }else{
                return temp;
            }
        }  else {
            return new ArrayList<>();
        }
    }

    @Override
    public XMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new XMap();

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
    private XMap _cookieMap;


    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();

            for(String k : _request.getHeaderNames()){
                _headerMap.put(k,_request.getHeader(k));
            }
        }

        return _headerMap;
    }
    private XMap _headerMap;

    //=================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _charset = charset;
    }
    private String _charset = "UTF-8";


    @Override
    protected void contentTypeDoSet(String contentType) {
        if (_charset != null) {
            if (contentType.indexOf(";") < 0) {
                headerSet("Content-Type", contentType + ";charset=" + _charset);
                return;
            }
        }

        headerSet("Content-Type", contentType);
    }


    @Override
    public OutputStream outputStream() throws IOException{
        return _outputStream;
    }

    @Override
    public void output(String str) {
        try {
            OutputStream out = _outputStream;

            out.write(str.getBytes(_charset));
            out.flush();
        }catch (Throwable ex){
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

            out.flush();
        }catch (Throwable ex){
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
        _response.addHeader(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {

        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(val).append(";");

        if (XUtil.isNotEmpty(path)) {
            sb.append("path=").append(path).append(";");
        }

        if (maxAge >= 0) {
            sb.append("max-age=").append(maxAge).append(";");
        }

        if (XUtil.isNotEmpty(domain)) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        _response.setHeader("Set-Cookie", sb.toString());
    }

    @Override
    public void redirect(String url)  {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) {
        try {
            headerSet("Location", url);
            status(code);
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int status() {
        return _status;
    }
    private int _status = 200;

    @Override
    public void status(int status) {
        _status = status;
        //_response.setHttpStatus(HttpStatus.valueOf(status));
    }

    @Override
    protected void commit() throws IOException{
        OutputStream out = _response.getOutputStream();
        _response.setHttpStatus(HttpStatus.valueOf(status()));
        _response.setContentLength(_outputStream.size());
        _outputStream.writeTo(out);
        out.close();
    }

}
