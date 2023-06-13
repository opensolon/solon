package org.noear.solon.web.sse;

import org.noear.solon.core.Lifecycle;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * web sse 简单实现
 *
 * @author kongweiguang
 * @since  2.3
 */
public class SseEmitter implements Lifecycle {
    private final Context ctx;
    private final LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
    private final AtomicBoolean b = new AtomicBoolean(false);

    private Runnable onCompletion;
    private Consumer<Throwable> onError;

    private final TimeUnit intervalUnit = TimeUnit.SECONDS;
    private final long interval;

    private final CompletableFuture future;


    /**
     * 完成之前回调方法
     *
     * @param onCompletion
     * @return
     */
    public SseEmitter onCompletion(Runnable onCompletion) {
        this.onCompletion = onCompletion;
        return this;
    }


    /**
     * 异常回调方法
     *
     * @param onError
     * @return
     */
    public SseEmitter onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }


    public SseEmitter(long interval) {
        this.ctx = Context.current();

        this.interval = interval;

        this.future = CompletableFuture.runAsync(() -> {
            try {
                executeSendTask();
            } catch (Throwable e) {
                if (onError != null) {
                    onError.accept(e);
                } else {
                    EventBus.pushTry(e);
                }
            }
        });
    }

    /**
     * 发送任务
     */
    private void executeSendTask() throws Throwable {
        sendHeader();
        ctx.flush();

        while (!b.get()) {
            String msg = q.poll();
            if (msg != null) {
                ctx.output(msg);
                ctx.flush();
            }

            if (intervalUnit != null) {
                intervalUnit.sleep(interval);
            }
        }
    }

    /**
     * 设置头
     */
    private void sendHeader() {
        ctx.headerSet("Content-Type", "text/event-stream");
        ctx.headerSet("Cache-Control", "no-cache");
        ctx.headerSet("Connection", "keep-alive");
    }

    /**
     * 发送消息内容
     * 默认都是使用对象的toString()方法
     *
     * @param o
     */
    public void send(String o) {
        send(new SseEvent().data(o));
    }

    public void send(SseEvent builder) {
        try {
            if (builder != null && !b.get()) {
                q.put(builder.build());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 任务开始
     */
    @Override
    public void start() throws Throwable {
        future.get();
    }


    /**
     * 任务关闭
     */
    @Override
    public void stop() throws Throwable {
        if (onCompletion != null) {
            onCompletion.run();
        }

        b.set(true);
    }
}
