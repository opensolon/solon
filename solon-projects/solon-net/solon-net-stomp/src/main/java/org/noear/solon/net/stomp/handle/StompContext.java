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
package org.noear.solon.net.stomp.handle;

import org.noear.solon.annotation.To;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.core.util.TmplUtil;
import org.noear.solon.net.stomp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Stomp 通用上下文适配
 *
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    static final Logger log = LoggerFactory.getLogger(StompContext.class);

    private StompSession session;
    private Frame frame;
    private String destination;
    private StompEmitter emitter;

    private boolean _isAsync;
    private ScheduledFuture<?> _asyncTimeoutFuture;
    private long _asyncTimeout = 30000L;//默认30秒
    private List<ContextAsyncListener> _asyncListeners = new ArrayList<>();

    protected boolean innerIsAsync() {
        return _isAsync;
    }

    public StompContext(StompSession session, Frame frame, String destination, StompEmitter emitter) {
        this.session = session;
        this.frame = frame;
        this.destination = destination;
        this.emitter = emitter;
    }

    /**
     * 数据帧
     */
    public Frame frame() {
        return frame;
    }

    /**
     * 发射器
     */
    public StompEmitter emitter() {
        return emitter;
    }

    //////////////

    /**
     * 请求对象
     */
    @Override
    public Object request() {
        return session;
    }

    @Override
    public String remoteIp() {
        try {
            return session.remoteAddress().getAddress().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int remotePort() {
        try {
            return session.remoteAddress().getPort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    /**
     * 会话Id
     */
    @Override
    public String sessionId() {
        return session.id();
    }

    /**
     * 请求方式
     */
    @Override
    public String method() {
        return MethodType.MESSAGE.name;
    }

    /**
     * 请求路径
     */
    @Override
    public String path() {
        return destination;
    }

    /**
     * 内容类型
     */
    @Override
    public String contentType() {
        //头名需小写
        return frame.getHeader(Headers.CONTENT_TYPE);
    }

    private InputStream bodyAsStream;

    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream == null) {
            bodyAsStream = new ByteArrayInputStream(frame.getPayload().getBytes(StandardCharsets.UTF_8));
        }

        return bodyAsStream;
    }

    /**
     * 请求主体
     *
     * @param charset 字符集
     */
    @Override
    public String body(String charset) throws IOException {
        return frame.getPayload();
    }

    /**
     * 请求头
     */
    @Override
    public MultiMap<String> headerMap() {
        if (headerMap == null) {
            headerMap = frame.getHeaderAll();
        }

        return headerMap;
    }

    @Override
    public Object response() {
        return session;
    }

    @Override
    public void contentType(String contentType) {
        headerSet(Headers.CONTENT_TYPE, contentType);
    }


    /**
     * 提取
     */
    @Override
    public Object pull(Class<?> clz) {
        if (Frame.class.isAssignableFrom(clz)) {
            return frame;
        }

        if (StompSession.class.isAssignableFrom(clz)) {
            return session;
        }

        return null;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void asyncStart(long timeout, ContextAsyncListener listener, Runnable runnable) {
        if (_isAsync == false) {
            _isAsync = true;

            if (listener != null) {
                _asyncListeners.add(listener);
            }

            if (timeout != 0) {
                _asyncTimeout = timeout;
            }

            if (_asyncTimeout > 0) {
                _asyncTimeoutFuture = RunUtil.delay(() -> {
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
                if (_asyncTimeoutFuture != null) {
                    _asyncTimeoutFuture.cancel(true);
                }
            }
        }
    }

    protected void innerCommit() throws Throwable {
        if (getHandled() || status() >= 200) {
            //payload
            ByteArrayOutputStream baos = (ByteArrayOutputStream) outputStream();

            if (baos.size() > 0) {
                //has data
                String returnValue = new String(baos.toByteArray());

                commit(returnValue);
            }
        } else {
            //...
        }
    }

    private void commit(Object returnValue) throws Throwable {
        //payload
        final Message message;
        if (returnValue instanceof Message) {
            message = (Message) returnValue;
        } else if (returnValue instanceof String) {
            message = new Message((String) returnValue);
        } else {
            message = new Message(renderAndReturn(returnValue));
        }

        //headers
        if (headerOfResponseMap != null) {
            for (KeyValues<String> kv : headerOfResponseMap) {
                //转为小写
                String key = kv.getKey().toLowerCase();
                for (String val : kv.getValues()) {
                    message.headerAdd(key, val);
                }
            }
        }

        //to anno
        Action action = action();
        To anno = null;
        if (action != null) {
            anno = action.method().getAnnotation(To.class);
        }

        //send-to
        if (anno == null || anno.value().length == 0) {
            //to from
            sendTo("*", path(), message);
        } else {
            for (String to : anno.value()) {
                //to target（支持模板）
                to = TmplUtil.parse(to, headerMap()::containsKey, headerMap()::get);

                int idx = to.indexOf(':');
                if (idx < 1) {
                    throw new IllegalArgumentException("Invalid to: " + to);
                } else {
                    sendTo(to.substring(0, idx), to.substring(idx + 1), message);
                }
            }
        }
    }

    private void sendTo(String user, String destination, Message message) {
        if ("*".equals(user)) {
            //to subscriber
            emitter().sendTo(destination, message);
        } else if (".".equals(user)) {
            //to session
            emitter().sendToSession(session, destination, message);
        } else {
            //to user
            emitter().sendToUser(user, destination, message);
        }
    }
}