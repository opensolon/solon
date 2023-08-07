package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public SseEmitterHandler(SseEmitter emitter) {
        this.ctx = Context.current();
        this.emitter = emitter;
    }

    /**
     * 开始
     */
    public void start() throws Throwable {
        ctx.contentType("text/event-stream;charset=utf-8");

        ctx.asyncStart(emitter.timeout, new AsyncListenerImpl(this));
        emitter.initialize(this);
    }


    /**
     * 发送事件内容
     *
     * @param event 事件数据
     */
    public synchronized void send(SseEvent event) throws IOException {
        if (event == null) {
            throw new IllegalArgumentException("SSE event cannot be null");
        }

        if (stopped.get()) {
            throw new IllegalStateException("SSE emitter was stopped");
        }

        try {
            ctx.output(event.build());
            ctx.flush();
        } catch (IOException e) {
            stopOnError(e);

            throw e;
        }
    }

    /**
     * 完成（用于手动控制）
     */
    public void complete() throws IOException{
        stop();
    }

    /**
     * 因出错停目
     */
    protected void stopOnError(Throwable e) throws IOException{
        if (emitter.onError != null) {
            emitter.onError.accept(e);
        }

        stop();
    }

    /**
     * 因操时停止（异步操时）
     */
    protected void stopOnTimeout() throws IOException{
        if (emitter.onTimeout != null) {
            emitter.onTimeout.run();
        }

        stop();
    }

    /**
     * 停止
     */
    protected synchronized void stop() throws IOException {
        if (stopped.get() == false) {
            stopped.set(true);

            if (emitter.onCompletion != null) {
                emitter.onCompletion.run();
            }

            ctx.asyncComplete();
        }
    }
}