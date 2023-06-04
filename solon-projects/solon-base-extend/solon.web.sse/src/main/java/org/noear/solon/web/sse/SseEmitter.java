package org.noear.solon.web.sse;

import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * solon sse简单实现
 */
public class SseEmitter {
    private final Context ctx;
    private final LinkedBlockingQueue<Object> q = new LinkedBlockingQueue<>();
    private final AtomicBoolean b = new AtomicBoolean(false);
    private final Runnable onCompletion;
    private final Consumer<Throwable> onError;
    private final TimeUnit unit;
    private final long timeout;
    private static final String t = "data: %s\n\n";

    private SseEmitter(Context ctx, Runnable onCompletion, Consumer<Throwable> onError, TimeUnit unit, long timeout) {
        this.ctx = ctx;
        this.onCompletion = onCompletion;
        this.onError = onError;
        this.unit = unit;
        this.timeout = timeout;
        init();
    }

    public static Builder builder() {
        return new Builder();
    }

    private void init() {
        CompletableFuture.runAsync(() -> {
            try {
                sendMsg();
            } catch (Throwable e) {
                onError.accept(e);
                throw new RuntimeException(e);
            }
        });
    }

    private void sendMsg() {
        setHeader();
        try {
            while (!b.get()) {
                Object take = q.poll();
                if (take != null) {
                    ctx.output(String.format(t, take));
                    ctx.flush();
                }
                if (unit != null) {
                    unit.sleep(timeout);
                }
            }
            ctx.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setHeader() {
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
    public void send(Object o) {
        try {
            if (o != null && !b.get()) {
                q.put(o);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void complete() {
        if (onCompletion != null) {
            onCompletion.run();
        }
        b.set(true);
    }

    /**
     * SseEmitter 构建器
     */
    public static class Builder {
        private Runnable onCompletion;
        private Consumer<Throwable> onError;
        private TimeUnit unit;
        private long timeout;

        /**
         * sse完成之前调用回调方法
         *
         * @param onCompletion
         * @return
         */
        public Builder onCompletion(Runnable onCompletion) {
            this.onCompletion = onCompletion;
            return this;
        }


        /**
         * sse异常时调用回调方法
         *
         * @param onError
         * @return
         */
        public Builder onError(Consumer<Throwable> onError) {
            this.onError = onError;
            return this;
        }

        /**
         * 发送消息间隔时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public Builder setInterval(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        /**
         * 构建SseEmitter对象
         *
         * @return
         */
        public SseEmitter build() {
            return new SseEmitter(Context.current(), onCompletion, onError, unit, timeout);
        }

    }


    /**
     * SseEventBuilder.
     */
    public static class SseEventBuilder {

        private final StringBuilder sb = new StringBuilder();

        /**
         * Add an SSE "id" line.
         */
        public SseEventBuilder id(String id) {
            append("id:").append(id).append("\n");
            return this;
        }

        /**
         * Add an SSE "event" line.
         */
        public SseEventBuilder name(String name) {
            append("event:").append(name).append("\n");
            return this;
        }

        /**
         * Add an SSE "retry" line.
         */
        public SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            append("retry:").append(String.valueOf(reconnectTimeMillis)).append("\n");
            return this;
        }

        /**
         * Add an SSE "data" line.
         */
        public SseEventBuilder data(Object object) {
            append("data:").append(object.toString()).append("\n");
            return this;
        }

        public String build() {
            return append("\n\n").sb.toString();
        }

        SseEventBuilder append(String text) {
            this.sb.append(text);
            return this;
        }

    }
}
