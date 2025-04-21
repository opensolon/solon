package org.noear.solon.web.rx.integration;

import org.noear.solon.rx.Completable;
import org.noear.solon.rx.handle.RxContext;
import org.noear.solon.rx.handle.RxHandler;
import org.reactivestreams.Publisher;

/**
 * @author noear 2025/2/16 created
 */
public class RxHandlerImpl implements RxHandler {
    private Publisher publisher;
    private boolean isStreaming;

    public RxHandlerImpl(Publisher publisher, boolean isStreaming) {
        this.publisher = publisher;
        this.isStreaming = isStreaming;
    }


    @Override
    public Completable handle(RxContext ctx) {
        return Completable.create(emitter -> {
            publisher.subscribe(new RxSubscriberImpl(ctx.toContext(), isStreaming, emitter));
        });
    }
}
