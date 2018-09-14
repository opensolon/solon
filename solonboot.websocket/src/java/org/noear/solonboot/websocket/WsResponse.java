package org.noear.solonboot.websocket;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class WsResponse implements Serializable {
    private Map<String,Object> _message = new HashMap<>();

    public WsResponse(WsRequest request) {
        this.requestId = request.getRequestId();
        this.outputStream = new ByteArrayOutputStream();
        this.writer = new PrintWriter(this.outputStream);
    }

    public Map<String,Object> message(){
        _message.put("request_id", this.requestId);
        _message.put("status",status);
        _message.put("characterEncoding",characterEncoding);
        _message.put("exception",exception);

        if(headers.size()>0){
            _message.put("headers",headers);
        }

        if(outputBody!=null){
            _message.put("body",outputBody);
        }

        return _message;
    }

    private String requestId;
    private int status;
    private final Map<String, String> headers = new HashMap<>();


    private byte[] outputBody;

    public String body() throws IOException {
        return new String(outputBody, getCharacterEncoding());
    }

    public InputStream bodyAsStream(){
        return new ByteArrayInputStream(outputBody);
    }

    private transient PrintWriter writer;
    private transient OutputStream outputStream;
    private String characterEncoding = "UTF-8";
    private Exception exception;

    public String getRequestId() {
        return requestId;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public PrintWriter getWriter() {
        return writer;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }
    //////////////////

    public void headerSet(String key, String val) {
        headers.put(key, val);
    }

    public String header(String key) {
        return headers.get(key);
    }

    public void close(){
        outputBody = ((ByteArrayOutputStream)outputStream).toByteArray();
    }
}
