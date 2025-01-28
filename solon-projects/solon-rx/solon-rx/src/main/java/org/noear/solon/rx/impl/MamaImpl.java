package org.noear.solon.rx.impl;

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.MamaEmitter;
import org.noear.solon.rx.base.BaseSubscription;
import org.reactivestreams.Subscriber;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public class MamaImpl<T> extends MamaBase<T> {
    private final Duration delay;
    private final Iterable<T> iterable;
    private final Consumer<MamaEmitter<T>> emitterConsumer;

    public MamaImpl(Duration delay, Iterable<T> iterable, Consumer<MamaEmitter<T>> emitterConsumer) {
        this.delay = delay;
        this.iterable = iterable;
        this.emitterConsumer = emitterConsumer;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        if (delay == null) {
            subscribeDo(subscriber);
        } else {
            RunUtil.delay(() -> {
                subscribeDo(subscriber);
            }, delay.toMillis());
        }
    }

    private void subscribeDo(Subscriber<? super T> subscriber) {
        //开始订阅
        BaseSubscription subscription = new BaseSubscription();
        subscriber.onSubscribe(subscription);

        if (iterable != null) {
            long l = subscription.getRequest();
            for (T item : iterable) {
                if (l > 0) {
                    subscriber.onNext(item);
                    l--;
                } else {
                    break;
                }
            }

            subscriber.onComplete();
            return;
        }


        if (emitterConsumer != null) {
            //转发发射器
            emitterConsumer.accept(new MamaEmitterImpl(subscriber));
            return;
        }

        //最后算完成
        subscriber.onComplete();
    }
}