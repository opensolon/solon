package org.noear.solon.rx;

import org.noear.solon.rx.base.BasePublisher;
import org.noear.solon.rx.impl.MamaImpl;
import org.noear.solon.rx.impl.MamaWrapper;
import org.reactivestreams.Publisher;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 多体发布者
 *
 * @author noear
 * @since 3.1
 */
public interface Mama<T> extends BasePublisher<T, Mama<T>> {
    <R> Mama<R> flatMap(Function<? super T, ? extends Publisher<? extends R>> mapper);

    Baba<List<T>> collectList();

    /// ////////////

    static <T> Mama<T> from(Publisher<T> publisher) {
        return new MamaWrapper<>(publisher);
    }

    static <T> Mama<T> create(Consumer<MamaEmitter<T>> emitterConsumer) {
        return new MamaImpl<>(null, null, emitterConsumer);
    }

    static <T> Mama<T> delay(Duration duration) {
        return new MamaImpl<>(duration, null, null);
    }

    static <T> Mama<T> just(T... items) {
        return new MamaImpl<>(null, Arrays.asList(items), null);
    }

    static <T> Mama<T> just(Iterable items) {
        return new MamaImpl<>(null, items, null);
    }
}