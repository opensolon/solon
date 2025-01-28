package org.noear.solon.rx;

import org.noear.solon.rx.base.BasePublisher;
import org.noear.solon.rx.impl.BabaImpl;
import org.noear.solon.rx.impl.BabaWrapper;
import org.reactivestreams.Publisher;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 单体发布者
 *
 * @author noear
 * @since 3.1
 */
public interface Baba<T> extends BasePublisher<T, Baba<T>> {
    <R> Baba<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper);

    /// ////////////

    static <T> Baba<T> from(Publisher<T> publisher) {
        if (publisher instanceof Baba) {
            return (Baba<T>) publisher;
        } else {
            return new BabaWrapper<>(publisher);
        }
    }

    static <T> Baba<T> create(Consumer<BabaEmitter<T>> emitterConsumer) {
        return new BabaImpl<>(null, null, null, emitterConsumer);
    }

    static Baba<Void> complete() {
        return new BabaImpl<>(null, null, null, null);
    }

    static Baba<Void> error(Throwable cause) {
        return new BabaImpl<>(null, cause, null, null);
    }


    static <T> Baba<T> delay(Duration duration) {
        return new BabaImpl<>(duration, null, null, null);
    }

    static <T> Baba<T> just(Supplier<T> item) {
        return new BabaImpl<>(null, null, item, null);
    }

    static <T> Baba<T> just(T item) {
        return new BabaImpl<>(null, null, () -> item, null);
    }
}
