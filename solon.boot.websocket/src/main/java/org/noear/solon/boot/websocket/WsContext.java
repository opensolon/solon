package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.core.*;
import org.noear.solon.XUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class WsContext extends XContextEmpty {

    private Charset _charset = Charset.forName("UTF-8");
    private InetSocketAddress _inetSocketAddress;
    private WebSocket _socket;
    private byte[] _message;
    public WsContext(WebSocket socket, byte[] message){
        _socket = socket;
        _message = message;
        _inetSocketAddress = socket.getRemoteSocketAddress();
    }

    @Override
    public String ip() {
        if(_inetSocketAddress == null)
            return null;
        else
            return _inetSocketAddress.getAddress().toString();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public String method() {
        return "SEND";
    }

    @Override
    public String protocol() {
        return "ws";
    }

    @Override
    public URI uri() {
        if(_uri == null) {
            _uri = URI.create(url());
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
        return "/";
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
    public String body() throws IOException {
        return new String(_message, _charset);
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return new ByteArrayInputStream(_message);
    }

    //==============

    @Override
    public Object response() {
        return null;
    }

    @Override
    public void charset(String charset) {
        _charset = Charset.forName(charset);
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type",contentType );
    }

    ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream outputStream() {
        return _outputStream;
    }

    @Override
    public void output(String str)  {
        try {
            _outputStream.write(str.getBytes(_charset));
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                _outputStream.write(buff, 0, rc);
            }

        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    public void commit(){
        _socket.send(_outputStream.toByteArray());
    }

}
