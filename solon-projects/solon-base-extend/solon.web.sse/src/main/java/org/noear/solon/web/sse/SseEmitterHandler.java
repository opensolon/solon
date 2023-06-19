package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author noear
 * @since 2.3
 */
public class SseEmitterHandler {
    private final SseEmitter emitter;
    private final Context ctx;
    private final LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
    private final AtomicBoolean b = new AtomicBoolean(false);

    public SseEmitterHandler(SseEmitter emitter) {
        this.ctx = Context.current();
        this.emitter = emitter;
    }

    public void init() throws Throwable {
        emitter.internalInit(this);

        internalSendHeader();

        if (ctx.asyncSupported()) {
            ctx.asyncStart(emitter.timeout, new AsyncListenerImpl(emitter));
        } else {
            executeSendTask();
        }
    }

    /**
     * 发送头
     */
    private void internalSendHeader() {
        ctx.headerSet("Content-Type", "text/event-stream");
        ctx.headerSet("Cache-Control", "no-cache");
        ctx.headerSet("Connection", "keep-alive");
        ctx.headerSet("Keep-Alive", "timeout=60");
    }

    /**
     * 发送事件
     */
    private void internalSendEvent(String event) throws IOException {
        ctx.output(event);
        ctx.flush();
    }

    /**
     * 发送事件任务（同步模式时）
     */
    private void executeSendTask() throws Throwable {
        while (!b.get()) {
            String event = q.poll();
            if (event != null) {
                internalSendEvent(event);
            }

            Thread.sleep(500L);
        }
    }

    /**
     * 发送事件内容
     *
     * @param event 事件数据
     */
    public synchronized void send(SseEvent event) throws IOException {
        if (event == null || b.get()) {
            return;
        }

        try {
            if (ctx.asyncSupported()) {
                internalSendEvent(event.build());
            } else {
                try {
                    q.put(event.build());
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        } catch (IOException e) {
            if (emitter.onError != null) {
                emitter.onError.accept(e);
            }

            throw e;
        }
    }

    /**
     * 完成
     */
    public synchronized void complete() {
        internalComplete();
        ctx.asyncComplete();
    }

    protected void internalComplete() {
        if (emitter.onCompletion != null) {
            emitter.onCompletion.run();
        }

        b.set(true);
    }
}