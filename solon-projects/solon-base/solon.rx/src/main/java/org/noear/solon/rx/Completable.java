package org.noear.solon.rx;

import org.noear.solon.rx.impl.CompletableImpl;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;

/**
 * 可完成的发布者（complete | error）
 *
 * @author noear
 * @since 2.0
 */
public interface Completable extends Publisher<Void> {
    static Completable create(Consumer<CompletableEmitter> emitterConsumer) {
        return new CompletableImpl(null, emitterConsumer);
    }

    static Completable complete() {
        return new CompletableImpl(null, null);
    }

    static Completable error(Throwable cause) {
        return new CompletableImpl(cause, null);
    }
}
