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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        return _request.getHeader("IP");
    }

    @Override
    public String method() {
        return _request.getMethodRange().toString();
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

    @Override
    public String url() {
        return _request.getRequestURI();
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
        return _request.getInputStream();
    }

    @Override
    public String[] paramValues(String key) {
        List<String> list = new ArrayList<>();
        try {
            for (String[] kv : _request.getParameterList()) {
                if (key.equals(kv[0])) {
                    list.add(kv[1]);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }

        return list.toArray(new String[]{});
    }

    @Override
    public String param(String key) {
        return paramMap().get(key);//param(key,null);//默认不能为null
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

    @Override
    public int paramAsInt(String key) {
        return Integer.parseInt(param(key,"0"));
    }

    @Override
    public long paramAsLong(String key) {
        return Long.parseLong(param(key,"0"));
    }

    @Override
    public double paramAsDouble(String key) {
        return Double.parseDouble(param(key,"0"));
    }


    private XMap _paramMap;
    @Override
    public XMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new XMap();

            try {
                _paramMap.putAll(_request.getParameterMap());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return _paramMap;
    }

    @Override
    public void paramSet(String key, String val) {
        paramMap().put(key, val);
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
            _cookieMap = new XMap();
            //_cookieMap = new XMap(_request.getHeader("Cookie"));
        }

        return _cookieMap;
    }
    private XMap _cookieMap;


    @Override
    public String header(String key) {
        return _request.getHeader(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeader(key);

        if (temp == null)
            return def;
        else
            return temp;
    }

    @Override
    public XMap headerMap() {
        if(_headerMap == null) {
            _headerMap = new XMap();

            _headerMap.putAll(_request.getHeadMap());
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
    public void contentType(String contentType) {
        headerSet("Content-Type",contentType );
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
    public void cookieSet(String key, String val, int maxAge) {
        cookieSet(key, val, null, maxAge);
    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {
        cookieSet(key, val, domain, "/", maxAge);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {

        StringBuilder sb = new StringBuilder();
        sb.append(key).append("=").append(val).append(";");

        if (path != null) {
            sb.append("path=").append(path).append(";");
        }

        sb.append("max-age=").append(maxAge).append(";");

        if (domain != null) {
            sb.append("domain=").append(domain.toLowerCase()).append(";");
        }

        _response.setHeader("Set-Cookie", sb.toString());
    }

    @Override
    public void cookieRemove(String key) {
        cookieSet(key,"",0);
    }

    @Override
    public void redirect(String url)  {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) {
        try {
            headerSet("Location", url);
            _response.setHttpStatus(HttpStatus.valueOf(code));
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
        _response.setHttpStatus(HttpStatus.valueOf(status()));
        _response.setContentLength(outputStream.size());
        outputStream.writeTo(_response.getOutputStream());
        _response.getOutputStream().close();
    }

}
