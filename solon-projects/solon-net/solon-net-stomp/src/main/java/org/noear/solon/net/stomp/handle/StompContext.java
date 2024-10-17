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

import org.noear.solon.Utils;
import org.noear.solon.annotation.To;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompEmitter;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.websocket.WebSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Stomp 通用上下文适配
 *
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    private WebSocket socket;
    private Frame frame;
    private String destination;
    private StompEmitter emitter;

    public StompContext(WebSocket socket, Frame frame, String destination, StompEmitter emitter) {
        this.socket = socket;
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
        return socket;
    }

    @Override
    public String remoteIp() {
        try {
            return socket.remoteAddress().getAddress().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int remotePort() {
        try {
            return socket.remoteAddress().getPort();
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
        return socket.id();
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
        return socket;
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

        if (WebSocket.class.isAssignableFrom(clz)) {
            return socket;
        }

        return null;
    }

    public void commit() throws Throwable {
        //payload
        ByteArrayOutputStream baos = (ByteArrayOutputStream) outputStream();

        if (baos.size() > 0) {
            //has data
            String returnValue = new String(baos.toByteArray());

            commit(returnValue);
        }
    }

    public void commit(Object returnValue) throws Throwable {
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
        message.headers(headerMap());

        //to anno
        Action action = action();
        To anno = null;
        if (action != null) {
            action.method().getAnnotation(To.class);
        }

        //send-to
        if (anno == null) {
            emitter().sendTo(path(), message);
        } else {
            for (String destination : anno.value()) {
                if (Utils.isEmpty(destination)) {
                    //如果是空的
                    emitter().sendTo(path(), message);
                } else {
                    emitter().sendTo(destination, message);
                }
            }
        }
    }
}