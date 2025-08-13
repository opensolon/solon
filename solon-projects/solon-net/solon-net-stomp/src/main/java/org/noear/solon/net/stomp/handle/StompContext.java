/*
 * Copyright 2017-2025 noear.org and authors
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

import org.noear.solon.Utils;
import org.noear.solon.server.handle.AsyncContextState;
import org.noear.solon.core.Constants;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.stomp.*;
import org.noear.solon.net.stomp.broker.impl.StompBrokerMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Stomp 通用上下文适配
 *
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    static final Logger log = LoggerFactory.getLogger(StompContext.class);

    private final StompSession session;
    private final Frame frame;
    private final String destination;
    private final StompBrokerMedia brokerMedia;

    public StompContext(StompSession session, Frame frame, String destination, StompBrokerMedia brokerMedia) {
        this.session = session;
        this.frame = frame;
        this.destination = destination;
        this.brokerMedia = brokerMedia;
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
        return brokerMedia.emitter;
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

        //to anno（支持模板；已通过拦截器处理模板）
        List<String> toList = this.attr(Constants.ATTR_TO);

        //send-to
        if (Utils.isEmpty(toList)) {
            if (brokerMedia.isBrokerDestination(path())) {
                //to broker
                sendTo("*", path(), message);
            } else {
                //to session
                sendTo(".", path(), message);
            }
        } else {
            for (String to : toList) {
                //to target(broker, app, user)
                int idx = to.indexOf(':');
                if (idx < 0) {
                    sendTo(to, path(), message);
                } else {
                    sendTo(to.substring(0, idx), to.substring(idx + 1), message);
                }
            }
        }
    }

    private void sendTo(String target, String destination, Message message) {
        if ("*".equals(target)) {
            //to subscriber
            emitter().sendTo(destination, message);
        } else if (".".equals(target)) {
            //to session
            emitter().sendToSession(session, destination, message);
        } else {
            //to user
            emitter().sendToUser(target, destination, message);
        }
    }


    ///////////////////////
    // for async
    ///////////////////////

    protected final AsyncContextState asyncState = new AsyncContextState();

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public boolean asyncStarted() {
        return asyncState.isStarted;
    }

    @Override
    public void asyncListener(ContextAsyncListener listener) {
        asyncState.addListener(listener);
    }

    @Override
    public void asyncStart(long timeout, Runnable runnable) {
        if (asyncState.isStarted == false) {
            asyncState.isStarted = true;
            asyncState.asyncDelay(timeout, this, this::innerCommit);

            if (runnable != null) {
                runnable.run();
            }

            asyncState.onStart(this);
        }
    }


    @Override
    public void asyncComplete() {
        if (asyncState.isStarted) {
            try {
                innerCommit();
            } catch (Throwable e) {
                log.warn("Async completion failed", e);
                asyncState.onError(this, e);
            } finally {
                asyncState.onComplete(this);
            }
        }
    }
}