package org.noear.solon.cloud.extend.folkmq.handle;

import org.noear.folkmq.client.MqAlarm;
import org.noear.folkmq.client.MqMessageReceivedImpl;
import org.noear.folkmq.common.MqConstants;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author noear
 * @since 2.7
 */
public class MqContext extends ContextEmpty {
    private MqMessageReceivedImpl _request;
    private EntityDefault _response;
    private MethodType _method;

    public MqContext(MqMessageReceivedImpl message) throws IOException {
        _request = message;
        _response = new EntityDefault();

        _method = MethodType.SOCKET;

        //传递 message meta
        if (Utils.isNotEmpty(message.getContent())) {
            for (Map.Entry<String, String> kv : message.getSource().metaMap().entrySet()) {
                if (kv.getKey().startsWith(MqConstants.MQ_ATTR_PREFIX)) {
                    headerMap().put(kv.getKey().substring(1), kv.getValue());
                }
            }
        }

        sessionState = new SessionStateEmpty();
    }


    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remotePort() {
        throw new UnsupportedOperationException();
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
        return "folkmq";
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
        return _request.getTag();
    }

    @Override
    public long contentLength() {
        return _request.getSource().dataSize();
    }

    @Override
    public String contentType() {
        return header("Content-Type");
    }

    @Override
    public String queryString() {
        return uri().getQuery();
    }

    private InputStream bodyAsStream;

    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream == null) {
            bodyAsStream = new ByteArrayInputStream(_request.getSource().dataAsBytes());
        }

        return bodyAsStream;
    }

    //==============

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type", contentType);
    }

    @Override
    public void headerSet(String key, String val) {
        _response.metaPut(key, val);
    }

    @Override
    public void headerAdd(String key, String val) {
        _response.metaPut(key, val);
    }

    @Override
    public String headerOfResponse(String name) {
        return _response.meta(name);
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

        long len = file.length();
        FileEntity fileEntity = new FileEntity(file);

        replyDo(fileEntity.data(), (int) len);

        fileEntity.release();
    }

    @Override
    public void outputAsFile(DownloadedFile file) throws IOException {
        //文件，直接答复处理
        String fileName = URLEncoder.encode(file.getName(), Solon.encoding());

        headerSet(EntityMetas.META_DATA_DISPOSITION_FILENAME, fileName);
        contentType(file.getContentType());

        byte[] bytes = IoUtil.transferToBytes(file.getContent());
        replyDo(ByteBuffer.wrap(bytes), (int) file.getContentSize());
    }

    protected void commit() throws IOException {
        replyDo(ByteBuffer.wrap(_outputStream.toByteArray()), _outputStream.size());
    }

    private void replyDo(ByteBuffer dataStream, int dataSize) throws IOException {
        if (dataSize > 0 || status() == 200) {
            _response.dataSet(dataStream);
            _request.response(_response);
        } else {
            if (errors == null) {
                _request.response(new MqAlarm("Service status code: " + status()));
            } else {
                _request.response(new MqAlarm(errors.getMessage()));
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

    }
}
