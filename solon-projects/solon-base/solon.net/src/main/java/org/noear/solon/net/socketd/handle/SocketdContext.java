package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;

/**
 * Socket.D Context + Hnalder 适配
 *
 * @author noear
 * @since 2.0
 */
public class SocketdContext extends ContextEmpty {
    static final Logger log = LoggerFactory.getLogger(SocketdContext.class);

    private Session _session;
    private Message _request;
    private EntityDefault _response;
    private MethodType _method;

    public SocketdContext(Session session, Message message) throws IOException {
        _session = session;
        _request = message;
        _response = new EntityDefault();

        _method = MethodType.SOCKET;

        //传递 sessoin param
        if (session.getHandshake().getParamMap().size() > 0) {
            headerMap().putAll(session.getHandshake().getParamMap());
        }

        //传递 message meta
        if (Utils.isNotEmpty(message.getMetaString())) {
            headerMap().putAll(message.getMetaMap());
        }

        sessionState = new SocketdSessionState(_session);
    }


    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        try {
            return _session.getRemoteAddress().getAddress().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int remotePort() {
        try {
            return _session.getRemoteAddress().getPort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
        return _session.getHandshake().getScheme();
    }


    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(url());
        }

        return _uri;
    }


    @Override
    public String url() {
        return _request.getTopic();
    }

    @Override
    public long contentLength() {
        if (_request.getData() == null) {
            return 0;
        } else {
            return _request.getDataSize();
        }
    }

    @Override
    public String contentType() {
        return header("Content-Type");
    }

    @Override
    public String queryString() {
        return uri().getQuery();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getData();
    }

    //==============

    @Override
    public Object response() {
        return _session;
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type", contentType);
    }

    @Override
    public void headerSet(String key, String val) {
        _response.putMeta(key,val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.putMeta(key,val);
    }

    @Override
    public String headerOfResponse(String name) {
        return _response.getMeta(name);
    }


    ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream outputStream() {
        return _outputStream;
    }

    @Override
    public void output(byte[] bytes) {
        try {
            outputStream().write(bytes);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            IoUtil.transferTo(stream, outputStream());
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void outputAsFile(File file) throws IOException {
        //文件，直接答复处理
        String fileName = URLEncoder.encode(file.getName(), Solon.encoding());
        String contentType = Utils.mime(file.getName());

        headerSet(EntityMetas.META_DATA_DISPOSITION_FILENAME, fileName);
        contentType(contentType);

        try (InputStream ins = new FileInputStream(file)) {
            replyDo(ins, (int) file.length());
        }
    }

    @Override
    public void outputAsFile(DownloadedFile file) throws IOException {
        //文件，直接答复处理
        String fileName = URLEncoder.encode(file.getName(), Solon.encoding());

        headerSet(EntityMetas.META_DATA_DISPOSITION_FILENAME, fileName);
        contentType(file.getContentType());

        try (InputStream ins = file.getContent()) {
            replyDo(ins, (int) file.getContentSize());
        }
    }

    protected void commit() throws IOException {
        replyDo(new ByteArrayInputStream(_outputStream.toByteArray()),  _outputStream.size());
    }

    private void replyDo(InputStream dataStream, int dataSize) throws IOException {
        if (_request.isRequest() || _request.isSubscribe()) {
            _response.data(dataStream);
            _session.replyEnd(_request, _response);
        } else {
            if (dataSize > 0) {
                log.warn("No reply is supported for the current message, key={}", _request.getSid());
            }
        }
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener) {
        //本身就是异步机制，不用启动
    }

    @Override
    public void asyncComplete() {

    }

    @Override
    public void close() throws IOException {
        _session.close();
    }
}
