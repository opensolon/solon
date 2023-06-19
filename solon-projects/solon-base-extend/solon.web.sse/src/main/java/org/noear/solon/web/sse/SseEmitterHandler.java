package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
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
    private final LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    public SseEmitterHandler(SseEmitter emitter) {
        this.ctx = Context.current();
        this.emitter = emitter;
    }

    /**
     * 开始
     * */
    public void start() throws Throwable {
        emitter.initialize(this);

        sendHeaderDo();

        if (ctx.asyncSupported()) {
            ctx.asyncStart(emitter.timeout, new AsyncListenerImpl(this));
        } else {
            executeSendTask();
        }
    }

    /**
     * 发送头
     */
    private void sendHeaderDo() {
        ctx.headerSet("Content-Type", "text/event-stream");
        ctx.headerSet("Cache-Control", "no-cache");
        ctx.headerSet("Connection", "keep-alive");
        ctx.headerSet("Keep-Alive", "timeout=60");
    }

    /**
     * 发送事件
     */
    private void sendEventDo(String event) throws IOException {
        ctx.output(event);
        ctx.flush();
    }

    /**
     * 发送事件任务（同步模式时）
     */
    private void executeSendTask() throws Throwable {
        while (!stopped.get()) {
            String event = q.poll();
            if (event != null) {
                sendEventDo(event);
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
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        if(stopped.get()){
            throw new IllegalStateException("Emitter was not initialized or stopped");
        }

        try {
            if (ctx.asyncSupported()) {
                sendEventDo(event.build());
            } else {
                try {
                    q.put(event.build());
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        } catch (IOException e) {
            stopOnError(e);

            throw e;
        }
    }

    /**
     * 完成（用于手动控制）
     */
    public synchronized void complete() {
        if (stop()) {
            if (ctx.asyncSupported()) {
                ctx.asyncComplete();
            }
        }
    }

    /**
     * 因出错停目
     */
    protected void stopOnError(Throwable e) {
        if (emitter.onError != null) {
            emitter.onError.accept(e);
        }

        stop();
    }

    /**
     * 因操时停止（异步操时）
     */
    protected void stopOnTimeout() {
        if (emitter.onTimeout != null) {
            emitter.onTimeout.run();
        }

        stop();
    }

    /**
     * 停止
     *
     * @return 是否正常停止
     */
    protected boolean stop() {
        if (stopped.get() == false) {
            stopped.set(true);

            if (emitter.onCompletion != null) {
                emitter.onCompletion.run();
            }

            return true;
        } else {
            return false;
        }
    }
}