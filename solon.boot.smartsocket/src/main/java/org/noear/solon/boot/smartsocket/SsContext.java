package org.noear.solon.boot.smartsocket;

import org.noear.solon.core.XContextEmpty;
import org.smartboot.socket.transport.AioSession;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;

public class SsContext extends XContextEmpty {

    private Charset _charset = Charset.forName("UTF-8");
    private InetSocketAddress _inetSocketAddress;
    private AioSession<byte[]> _session;
    private InputStream _inputStream;

    public SsContext(AioSession<byte[]> session, InputStream inputStream){
        _session = session;
        _inputStream = inputStream;

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
            return _inetSocketAddress.getAddress().toString();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public String method() {
        return "LISTEN";
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
        InputStream inpStream = _inputStream;

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
        return _inputStream;
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
//        _socket.close();
    }
}
