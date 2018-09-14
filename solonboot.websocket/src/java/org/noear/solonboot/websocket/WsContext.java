package org.noear.solonboot.websocket;

import org.noear.solonboot.XMap;
import org.noear.solonboot.XUtil;
import org.noear.solonboot.protocol.XContext;
import org.noear.solonboot.protocol.XHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
    public String userAgent() {
        return header(XHeader.USER_AGENT);
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
        return header(XHeader.CONTENT_TYPE);
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
    public String param(String key) {
        return _request.getParams().get(key);
    }

    @Override
    public String param(String key, String def) {
        try {
            String temp = _request.getParams().get(key);

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

    @Override
    public XMap paramMap() {
        try {
            return new XMap(_request.getParams());
        }catch (Exception ex){
            ex.printStackTrace();
            return new XMap();
        }
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

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public Object session(String key){
        return null;
    }
    @Override
    public void sessionSet(String key, Object val) {

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
        _response.headerSet(XHeader.CONTENT_TYPE, contentType);
    }

    @Override
    public void output(String str) throws IOException {
        _response.getOutputStream().write(str.getBytes(_response.getCharacterEncoding()));
    }

    @Override
    public void output(InputStream stream) throws IOException {
        OutputStream out = _response.getOutputStream();

        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            out.write(buff, 0, rc);
        }

        out.flush();
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
    public void cookieRemove(String key) {

    }

    @Override
    public void redirect(String url) throws IOException {
        redirect(url,301);
    }

    @Override
    public void redirect(String url, int code) throws IOException {
        headerSet(XHeader.LOCATION, url);
        _response.setStatus(code);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    public void status(int status) throws IOException {
        _response.setStatus(status);
    }
}
