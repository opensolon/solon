package org.noear.solonboot.smartsocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SsRequest {
    private Map<String,Object> _message;
    private String requestId;
    public SsRequest(Map<String,Object> message) {
        _message = message;

        requestId = (String) message.get("request_id");

        if(_message.containsKey("params")){
            params = (Map<String, String>)_message.get("params");
        }else{
            params = new HashMap<>();
        }

        if(_message.containsKey("headers")){
            headers = (Map<String, String>)_message.get("headers");
        }else{
            headers = new HashMap<>();
        }

        if(_message.containsKey("body")){
            body = new ByteArrayInputStream((byte[])_message.get("body"));
        }
    }

    private  Map<String, String> params ;
    private  Map<String, String> headers;
    private ByteArrayInputStream body;

    private InetSocketAddress remoteAddr;

    public String getRequestId() {
        return requestId;
    }

    public String getRequestUrl() {
        return (String) _message.get("request_url");
    }

    public String getProtocol() {
        return (String) _message.get("protocol");
    }

    public String getMethod() {
        return (String) _message.get("method");
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    /////////////////////
    public void paramSet(String key, String val) {
        params.put(key, val);
    }

    public String param(String key) {
        return params.get(key);
    }

    public void headerSet(String key, String val) {
        headers.put(key, val);
    }

    public String header(String key) {
        return headers.get(key);
    }

    public void setRemoteAddr(InetSocketAddress remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public InetSocketAddress getRemoteAddr() {
        return remoteAddr;
    }
}
