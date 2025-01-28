package org.noear.solon.rx.base;

import org.reactivestreams.Publisher;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public interface BasePublisher<T, Self extends BasePublisher> extends Publisher<T> {

    Self doOnNext(Consumer<? super T> doOnNext);

    Self doOnError(Consumer<Throwable> doOnError);

    Self doOnComplete(Runnable doOnComplete);

    void subscribe();
}
