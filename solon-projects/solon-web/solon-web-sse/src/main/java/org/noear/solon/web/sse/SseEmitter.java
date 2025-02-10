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
package org.noear.solon.web.sse;

import org.noear.solon.core.util.ConsumerEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Sse 发射器（操作界面）
 *
 * @author kongweiguang
 * @since  2.3
 */
public class SseEmitter {
    static final Logger log = LoggerFactory.getLogger(SseEmitter.class);

    private SseEmitterHandler handler;
    protected Runnable onCompletion;
    protected Runnable onTimeout;
    protected Function<SseEvent, SseEvent> onSendPost;
    protected Consumer<Throwable> onError;
    protected ConsumerEx<SseEmitter> onInited;

    protected long timeout;


    /**
     * 完成回调方法
     */
    public SseEmitter onCompletion(Runnable onCompletion) {
        this.onCompletion = onCompletion;
        return this;
    }

    /**
     * 超时回调方法
     */
    public SseEmitter onTimeout(Runnable onTimeout) {
        this.onTimeout = onTimeout;
        return this;
    }

    /**
     * 发送确认方法
     */
    public SseEmitter onSendPost(Function<SseEvent, SseEvent> onSendPost) {
        this.onSendPost = onSendPost;
        return this;
    }

    /**
     * 异常回调方法
     */
    public SseEmitter onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    /**
     * 初始化回调方法
     */
    public SseEmitter onInited(ConsumerEx<SseEmitter> onInited) {
        this.onInited = onInited;
        return this;
    }


    /**
     * 超时（用于异步超时）
     */
    public SseEmitter(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 发送事件内容
     *
     * @param data 事件数据
     */
    public void send(String data) throws IOException {
        send(new SseEvent().data(data));
    }

    /**
     * 发送事件
     *
     * @param event 事件数据
     */
    public void send(SseEvent event) throws IOException {
        if (onSendPost != null) {
            event = onSendPost.apply(event);
        }

        if (event != null) {
            handler.send(event);
        }
    }

    /**
     * 完成（用于手动控制）
     */
    public void complete() {
        try {
            handler.complete();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
    }


    /**
     * 初始化
     */
    protected void initialize(SseEmitterHandler handler) throws Throwable {
        this.handler = handler;

        if (onInited != null) {
            onInited.accept(this);
        }
    }
}
