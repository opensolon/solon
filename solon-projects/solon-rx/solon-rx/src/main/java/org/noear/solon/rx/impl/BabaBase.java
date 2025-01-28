package org.noear.solon.rx.impl;

import org.noear.solon.rx.Baba;
import org.reactivestreams.Publisher;

import java.util.function.Function;

/**
 * @author noear 2025/1/28 created
 */
public abstract class BabaBase<T> extends AbstractPublisher<T, Baba<T>> implements Baba<T> {
    @Override
    public <R> Baba<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return new BabaMapper<>(this, mapper);
    }
}
