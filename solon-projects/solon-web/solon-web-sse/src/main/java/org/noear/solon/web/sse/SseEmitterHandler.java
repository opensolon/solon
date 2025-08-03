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

import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sse 发射处理器
 *
 * @author noear
 * @since 2.3
 */
public class SseEmitterHandler {
    private final SseEmitter emitter;
    private final Context ctx;
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final ReentrantLock SYNC_LOCK = new ReentrantLock(true);

    public SseEmitterHandler(SseEmitter emitter) {
        this.ctx = Context.current();
        this.emitter = emitter;
    }

    /**
     * 开始
     */
    public void start() throws Throwable {
        SseRender.pushSseHeaders(ctx);

        ctx.asyncListener(new AsyncListenerImpl(this));
        ctx.asyncStart(emitter.timeout, null);

        emitter.initialize(this);
    }

    private static final byte[] CRLF = "\n".getBytes();

    /**
     * 出错
     */
    public void error(Throwable t) {
        if (t != null) {
            ctx.status(500, t.getMessage());
        }
    }

    /**
     * 发送事件内容
     *
     * @param event 事件数据
     */
    public void send(SseEvent event) throws IOException {
        if (event == null) {
            throw new IllegalArgumentException("SSE event cannot be null");
        }

        if (stopped.get()) {
            throw new IllegalStateException("SSE emitter was stopped");
        }

        SYNC_LOCK.lock();
        try {
            if (ctx.isHeadersSent() == false) {
                ctx.contentType(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE);
            }

            ctx.output(event.toString());
            ctx.output(CRLF);
            ctx.flush();
        } catch (Throwable e) {
            stopOnError(e);

            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new IOException(e);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 完成（用于手动控制）
     */
    public void complete() throws IOException {
        stop();
    }

    /**
     * 因出错停目
     */
    protected void stopOnError(Throwable e) throws IOException {
        if (emitter.onError != null) {
            emitter.onError.accept(e);
        }

        stop();
    }

    /**
     * 因操时停止（异步操时）
     */
    protected void stopOnTimeout() throws IOException {
        if (emitter.onTimeout != null) {
            emitter.onTimeout.run();
        }

        stop();
    }

    /**
     * 停止
     */
    protected void stop() throws IOException {
        if (stopped.get() == false) {
            stopped.set(true);

            SYNC_LOCK.lock();
            try {
                if (emitter.onCompletion != null) {
                    emitter.onCompletion.run();
                }

                ctx.asyncComplete();
            } finally {
                SYNC_LOCK.unlock();
            }
        }
    }
}