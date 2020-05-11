package org.noear.solon.boot.jlhttp;

import org.noear.solon.core.XMap;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;

import javax.sound.sampled.FloatControl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

public class JlHttpContext extends XContext {
    private HTTPServer.Request _request;
    private HTTPServer.Response _response;
    protected Map<String,List<XFile>> _fileMap;

    public JlHttpContext(HTTPServer.Request request, HTTPServer.Response response) {
        _request = request;
        _response = response;

        //文件上传需要
        if (isMultipart()) {
            try {
                _fileMap = new HashMap<>();
                MultipartUtil.buildParamsAndFiles(this);
            }catch (Throwable ex){
                ex.printStackTrace();
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
        return _request.getVersion();
    }

    @Override
    public URI uri() {
        return URI.create(url());
    }

    @Override
    public String path() {
        return uri().getPath();
    }

    private String _url;
    @Override
    public String url() {
        if(_url == null){
            _url = _request.getURI().toString();

            if (_url != null && _url.startsWith("/")) {
                String host = header("Host");

                if (host == null) {
                    host = header(":authority");
                    String scheme = header(":scheme");

                    if(host == null){
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
        }

        return _url;
    }

    @Override
    public long contentLength() {
        try {
            return _request.getBody().available();
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
    public String body() throws IOException {
        InputStream inpStream = bodyAsStream();

        StringBuilder content = new StringBuilder();
        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = inpStream.read(b)) > 0) {
            content.append(new String(b, 0, lens));
        }

        return content.toString();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getBody();
    }

    @Override
    public String[] paramValues(String key) {
        List<String> list = paramsMap().get(key);
        if(list == null){
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
                _paramMap.putAll(_request.getParams());
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
                ex.printStackTrace();
                return null;
            }
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
    public String cookie(String key) {
        return cookieMap().get(key);
    }

    @Override
    public String cookie(String key, String def) {
        String temp = cookieMap().get(key);
        if(temp == null){
            return  def;
        }else{
            return temp;
        }
    }

    @Override
    public XMap cookieMap() {
        if(_cookieMap == null){
            _cookieMap = new XMap(_request.getHeaders().getParams("Cookie"));
        }

        return _cookieMap;
    }
    private XMap _cookieMap;


    @Override
    public String header(String key) {
        return _request.getHeaders().get(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeaders().get(key);

        if (temp == null)
            return def;
        else
            return temp;
    }

    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();

            HTTPServer.Headers headers = _request.getHeaders();

            if (headers != null) {
                for (HTTPServer.Header h : headers) {
                    _headerMap.put(h.getName(), h.getValue());
                }
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
        return outputStream;
    }

    @Override
    public void output(String str) {
        try {
            OutputStream out = outputStream;

            out.write(str.getBytes(_charset));
            out.flush();
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = outputStream;

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

    protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();



    @Override
    public void headerSet(String key, String val) {
        _response.getHeaders().replace(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.getHeaders().add(key,val);
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

        _response.getHeaders().add("Set-Cookie", sb.toString());
    }

    @Override
    public void redirect(String url)  {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) {
        try {
            headerSet("Location", url);
            _response.sendHeaders(code);
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
        //_response.sendHeaders(status); //jlhttp 的 状态，由 上下文代理 负责
    }


    //jlhttp 需要先输出 header ，但是 header 后面可能会有变化；所以不直接使用  response.getOutputStream()
    @Override
    protected void commit() throws IOException{
        outputStream.writeTo(_response.getOutputStream());
        _response.getOutputStream().close();
    }

}
