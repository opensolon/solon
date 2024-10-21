/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.EntityDefault;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.IoUtil;
import org.noear.solon.core.util.RunUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private boolean _isAsync;
    private long _asyncTimeout = 30000L;//默认30秒
    private CompletableFuture<Object> _asyncFuture;
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    public SocketdContext(Session session, Message message) throws IOException {
        _session = session;
        _request = message;
        _response = new EntityDefault();

        //传递 sessoin param
        if (session.handshake().paramMap().size() > 0) {
            headerMap().putAll(session.handshake().paramMap());
        }

        //传递 message meta
        if (Utils.isNotEmpty(message.metaString())) {
            headerMap().putAll(message.metaMap());
        }

        sessionState = new SocketdSessionState(_session);
    }

    protected Session session() {
        return _session;
    }

    protected Message message() {
        return _request;
    }


    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String remoteIp() {
        try {
            return _session.remoteAddress().getAddress().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int remotePort() {
        try {
            return _session.remoteAddress().getPort();
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
        return MethodType.SOCKET.name;
    }

    @Override
    public String protocol() {
        return _session.handshake().uri().getScheme();
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
        return _request.event();
    }

    private String path;

    /**
     * 获取请求的URI路径
     */
    public String path() {
        if (path == null && url() != null) {
            path = uri().getPath();
            if (path == null) {
                this.path = "";
            }
            if (path.contains("//")) {
                path = Utils.trimDuplicates(path, '/');
            }
        }

        return path;
    }

    @Override
    public long contentLength() {
        return _request.dataSize();
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
            bodyAsStream = new ByteArrayInputStream(_request.dataAsBytes());
        }

        return bodyAsStream;
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

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener, Runnable runnable) {
        if (_isAsync == false) {
            _isAsync = true;

            _asyncFuture = new CompletableFuture<>();

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }

            if (_asyncTimeout > 0) {
                RunUtil.delay(() -> {
                    for (ContextAsyncListener listener1 : _asyncListeners) {
                        try {
                            listener1.onTimeout(this);
                        } catch (IOException e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                }, _asyncTimeout);
            }

            if (runnable != null) {
                runnable.run();
            }
        }
    }


    @Override
    public void asyncComplete() {
        if (_isAsync) {
            try {
                innerCommit();
            } catch (Throwable e) {
                log.warn("Async completion failed ", e);
            } finally {
                _asyncFuture.complete(this);
            }
        }
    }

    protected void innerCommit() throws IOException {
        if (getHandled() || status() >= 200) {
            ByteArrayOutputStream out = (ByteArrayOutputStream) outputStream();
            replyDo(ByteBuffer.wrap(out.toByteArray()), out.size());
        } else {
            _session.sendAlarm(_request, "No event handler was found! like code=404");
        }
    }

    private void replyDo(ByteBuffer dataStream, int dataSize) throws IOException {
        if (_request.isRequest() || _request.isSubscribe()) {
            _response.dataSet(dataStream);
            _session.replyEnd(_request, _response);
        } else {
            if (dataSize > 0) {
                log.warn("No reply is supported for the current message, sid={}", _request.sid());
            }
        }
    }

    @Override
    public void close() throws IOException {
        _session.close();
    }
}