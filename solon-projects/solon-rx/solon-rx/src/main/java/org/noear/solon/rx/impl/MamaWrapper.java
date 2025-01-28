package org.noear.solon.rx.impl;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * @author noear 2025/1/28 created
 */
public class MamaWrapper<T> extends MamaBase<T> {
    private final Publisher<T> publisher;

    public MamaWrapper(Publisher<T> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        publisher.subscribe(subscriber);
    }
}
