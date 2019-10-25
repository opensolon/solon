package org.noear.solon.boot.websocket;

import org.noear.solon.core.XMap;
import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WsContext extends XContext{

    private WsRequest _request;
    private WsResponse _response;

    public WsContext(WsRequest request, WsResponse response){
        this._request = request;
        this._response = response;
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        if(_request.getRemoteAddr() == null)
            return null;
        else
            return _request.getRemoteAddr().getAddress().toString();
    }

    @Override
    public boolean isMultipart() {
        return false;
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
        if(_uri == null) {
            _uri = URI.create(_request.getRequestUrl());
        }

        return _uri;
    }
    private URI _uri;

    @Override
    public String path() {
        return uri().getPath();
    }



    @Override
    public String url() {
        return _request.getRequestUrl();
    }

    @Override
    public long contentLength() {
        try {
            if (_request.getBody() != null) {
                return _request.getBody().available();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
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
        return paramMap().values().toArray(new String[]{});
    }

    @Override
    public String param(String key) {
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
        if(_paramMap!=null){
            _paramMap = new XMap();

            try {
                _paramMap.putAll(_request.getParams());
            }catch (Exception ex){
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
    public List<XFile> files(String key) {
        return new ArrayList<>();
    }

    @Override
    public String cookie(String key) {
        return null;
    }

    @Override
    public String cookie(String key, String def) {
        return null;
    }

    @Override
    public XMap cookieMap() {
        return null;
    }

    @Override
    public String header(String key) {
        return _request.header(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.header(key);

        if (temp == null)
            return def;
        else
            return temp;
    }

    @Override
    public XMap headerMap() {
        return new XMap(_request.getHeaders());
    }


    //==============

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _response.setCharacterEncoding(charset);
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type",contentType );
    }

    @Override
    public OutputStream outputStream() {
        return _response.getOutputStream();
    }

    @Override
    public void output(String str)  {
        try {
            _response.getOutputStream().write(str.getBytes(_response.getCharacterEncoding()));
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = _response.getOutputStream();

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

    @Override
    public void headerSet(String key, String val) {
        _response.headerSet(key,val);
    }


    @Override
    public void cookieSet(String key, String val, int maxAge) {

    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {

    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {

    }

    @Override
    public void cookieRemove(String key) {

    }

    @Override
    public void redirect(String url) {
        redirect(url,302);
    }

    @Override
    public void redirect(String url, int code) {
        headerSet("Location", url);
        _response.setStatus(code);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    public void status(int status)  {
        _response.setStatus(status);
    }
}
