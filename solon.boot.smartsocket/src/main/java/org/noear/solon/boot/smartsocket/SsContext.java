package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.SocketRequest;
import org.noear.solon.core.XContextEmpty;
import org.noear.solon.core.XMethod;
import org.smartboot.socket.transport.AioSession;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SsContext extends XContextEmpty {

    private Charset _charset = StandardCharsets.UTF_8;
    private InetSocketAddress _inetSocketAddress;
    private AioSession<SocketRequest> _session;
    private SocketRequest _request;

    public SsContext(AioSession<SocketRequest> session, SocketRequest request){
        _session = session;
        _request = request;

        try {
            _inetSocketAddress = session.getRemoteAddress();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object request() {
        return _session;
    }

    @Override
    public String ip() {
        if(_inetSocketAddress == null)
            return null;
        else
            return _inetSocketAddress.getAddress().getHostAddress();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public String method() {
        return XMethod.LISTEN.name;
    }

    @Override
    public String protocol() {
        return "SOCKET";
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
        return null;
        //return _session.getResourceDescriptor();
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
        InputStream inpStream = _request.message;

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
        return _request.message;
    }

    //==============

    @Override
    public Object response() {
        return _session;
    }

    @Override
    public void charset(String charset) {
        _charset = Charset.forName(charset);
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type",contentType );
    }


    @Override
    public OutputStream outputStream() {
        return _session.writeBuffer();
    }

    @Override
    public void output(String str)  {
        try {
            outputStream().write(str.getBytes(_charset));
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
                outputStream().write(buff, 0, rc);
            }

        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }


    @Override
    protected void commit() throws IOException {

    }

    @Override
    public void close() throws IOException {
        _session.close();
    }
}
