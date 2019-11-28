package org.noear.solon.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XContextEmpty extends XContext {
    public static XContext create(){
        return new XContextEmpty();
    }

    public XContextEmpty(){
        sessionStateInit(new XSessionState() {
            @Override
            public String sessionId() {
                return null;
            }

            @Override
            public Object sessionGet(String key) {
                return sessionMap().get(key);
            }

            @Override
            public void sessionSet(String key, Object val) {
                sessionMap().put(key,val);
            }
        });
    }


    private Map<String,Object> _sessionMap = null;
    public Map<String,Object> sessionMap(){
        if(_sessionMap == null){
            _sessionMap = new HashMap<>();
        }

        return _sessionMap;
    }



    @Override
    public Object request() {
        return null;
    }

    @Override
    public String ip() {
        return null;
    }

    @Override
    public String method() {
        return null;
    }

    @Override
    public String protocol() {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public String body()  {
        return null;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return null;
    }

    @Override
    public String[] paramValues(String key) {
        return new String[0];
    }

    @Override
    public String param(String key) {
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        return paramMap().getOrDefault(key, def);
    }

    private XMap _paramMap = null;
    @Override
    public XMap paramMap() {
        if(_paramMap == null){
            _paramMap = new XMap();
        }
        return _paramMap;
    }

    @Override
    public List<XFile> files(String key) throws Exception {
        return null;
    }

    @Override
    public String cookie(String key) {
        return cookieMap().get(key);
    }

    @Override
    public String cookie(String key, String def) {
        return cookieMap().getOrDefault(key,def);
    }

    XMap _cookieMap = null;
    @Override
    public XMap cookieMap() {
        if(_cookieMap == null){
            _cookieMap = new XMap();
        }
        return _cookieMap;
    }

    @Override
    public String header(String key) {
        return headerMap().get(key);
    }

    @Override
    public String header(String key, String def) {
        return headerMap().getOrDefault(key,def);
    }

    private XMap _headerMap = null;
    @Override
    public XMap headerMap() {
        if(_headerMap == null){
            _headerMap = new XMap();
        }
        return _headerMap;
    }

    @Override
    public Object response() {
        return null;
    }

    @Override
    public void charset(String charset) {

    }

    @Override
    protected void contentTypeDoSet(String contentType) {

    }

    @Override
    public void output(String str) {

    }

    @Override
    public void output(InputStream stream) {

    }

    @Override
    public OutputStream outputStream() {
        return null;
    }

    @Override
    public void headerSet(String key, String val) {
        headerMap().put(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        cookieMap().put(key,val);
    }

    @Override
    public void redirect(String url) {

    }

    @Override
    public void redirect(String url, int code) {

    }

    private int _status = 0;
    @Override
    public int status() {
        return _status;
    }

    @Override
    public void status(int status)  {
        _status = status;
    }
}
