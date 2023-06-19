package org.noear.solon.web.sse;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Sse 发射器
 *
 * @author kongweiguang
 * @since  2.3
 */
public class SseEmitter {
    private SseEmitterHandler handler;
    protected Runnable onCompletion;
    protected Runnable onTimeout;
    protected Consumer<Throwable> onError;


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
     * 异常回调方法
     */
    public SseEmitter onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    public SseEmitter release(Consumer<SseEmitter> consumer){
        consumer.accept(this);
        return this;
    }


    public SseEmitter(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 发送事件内容
     *
     * @param data 事件数据
     */
    public void send(String data) throws IOException {
        handler.send(new SseEvent().data(data));
    }

    /**
     * 发送事件内容
     *
     * @param event 事件数据
     */
    public void send(SseEvent event) throws IOException {
        handler.send(event);
    }

    /**
     * 完成
     */
    public void complete() {
        handler.complete();
    }


    /**
     * 内部初始化
     */
    protected void internalInit(SseEmitterHandler handler) {
        this.handler = handler;
    }

    /**
     * 内部完成调用
     */
    protected void internalComplete() {
        handler.internalComplete();
    }
}
