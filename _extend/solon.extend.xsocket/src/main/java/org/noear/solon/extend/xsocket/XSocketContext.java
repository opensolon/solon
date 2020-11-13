package org.noear.solon.extend.xsocket;

import org.noear.solon.core.XContextEmpty;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * XSocket 上下文
 *
 * @author noear
 * @since 1.0
 * */
public class XSocketContext extends XContextEmpty {
    private InetSocketAddress _inetSocketAddress;
    private XSession _sesssion;
    private XMessage _message;
    private boolean _messageIsString;
    private XMethod _method;

    public XSocketContext(XSession session, XMessage message, boolean messageIsString) {
        _sesssion = session;
        _message = message;
        _messageIsString = messageIsString;
        _method = session.method();
        _inetSocketAddress = session.getRemoteAddress();
    }


    @Override
    public Object request() {
        return _sesssion;
    }

    @Override
    public String ip() {
        if (_inetSocketAddress == null)
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
        return _method.name;
    }

    @Override
    public String protocol() {
        if (_method.code == XMethod.WEBSOCKET.code) {
            return "WS";
        } else {
            return "SOCKET";
        }
    }

    @Override
    public URI uri() {
        if (_uri == null) {
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
        return _message.resourceDescriptor();
    }

    @Override
    public long contentLength() {
        if (_message.content() == null) {
            return 0;
        } else {
            return _message.content().length;
        }
    }

    @Override
    public String contentType() {
        return headerMap().get("Content-Type");
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return new ByteArrayInputStream(_message.content());
    }

    //==============

    @Override
    public Object response() {
        return _sesssion;
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type", contentType);
    }

    ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream outputStream() {
        return _outputStream;
    }

    @Override
    public void output(byte[] bytes) {
        try {
            _outputStream.write(bytes);
        } catch (Exception ex) {
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

        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void commit() throws IOException {
        if (_sesssion.isValid()) {
            if (_messageIsString) {
                _sesssion.send(_outputStream.toString());
            } else {
                XMessage msg = XMessage.wrap(1, _message.key(), _message.resourceDescriptor(), _outputStream.toByteArray());
                _sesssion.send(msg);
            }
        }
    }

    @Override
    public void close() throws IOException {
        _sesssion.close();
    }
}
