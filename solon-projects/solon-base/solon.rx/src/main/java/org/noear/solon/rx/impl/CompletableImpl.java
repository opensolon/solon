package org.noear.solon.rx.impl;

import org.noear.solon.rx.Completable;
import org.noear.solon.rx.CompletableEmitter;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

/**
 * 可完成的发布者实现
 *
 * @author noear
 * @since 2.9
 */
public class CompletableImpl implements Completable, Subscription {
    private final Throwable cause;
    private Consumer<CompletableEmitter> emitterConsumer;

    public CompletableImpl(Throwable cause, Consumer<CompletableEmitter> emitterConsumer) {
        this.cause = cause;
        this.emitterConsumer = emitterConsumer;
    }

    @Override
    public void subscribe(Subscriber<? super Void> subscriber) {
        //开始订阅
        subscriber.onSubscribe(this);

        if (emitterConsumer == null) {
            if (cause == null) {
                //完成
                subscriber.onComplete();
            } else {
                //出错
                subscriber.onError(cause);
            }
        } else {
            //转发发射器
            emitterConsumer.accept(new CompletableEmitterImpl(subscriber));
        }
    }

    @Override
    public void request(long l) {

    }

    @Override
    public void cancel() {

    }
}
