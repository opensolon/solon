package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.core.*;
import org.noear.solonclient.channel.SocketMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

public class WsContext extends XContextEmpty {
    private InetSocketAddress _inetSocketAddress;
    private WebSocket _socket;
    private SocketMessage _message;
    private boolean _messageIsString;
    public WsContext(WebSocket socket, SocketMessage message, boolean messageIsString){
        _socket = socket;
        _message = message;
        _inetSocketAddress = socket.getRemoteSocketAddress();
        _messageIsString = messageIsString;
    }

    @Override
    public Object request() {
        return _socket;
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
        return XMethod.WEBSOCKET.name;
    }

    @Override
    public String protocol() {
        return "WS";
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
        return _message.resourceDescriptor;
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
    public InputStream bodyAsStream() throws IOException {
        return new ByteArrayInputStream(_message.content);
    }

    //==============

    @Override
    public Object response() {
        return _socket;
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

    @Override
    protected void commit() throws IOException {
        if (_socket.isOpen()) {
            if (_messageIsString) {
                _socket.send(new String(_outputStream.toByteArray()));
            } else {
                _socket.send(_outputStream.toByteArray());
            }
        }
    }

    @Override
    public void close() throws IOException {
        _socket.close();
    }
}
