package org.noear.solon.web.sse;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ConsumerEx;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Sse 发射器（操作界面）
 *
 * @author kongweiguang
 * @since  2.3
 */
public class SseEmitter {
    private SseEmitterHandler handler;
    protected Runnable onCompletion;
    protected Runnable onTimeout;
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
        handler.send(event);
    }

    /**
     * 完成（用于手动控制）
     */
    public void complete() {
        try {
            handler.complete();
        } catch (IOException e) {
            EventBus.pushTry(e);
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
