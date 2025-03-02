package org.noear.solon.web.rx.integration;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.handle.RxContext;
import org.noear.solon.rx.handle.RxHandler;
import org.reactivestreams.Publisher;

/**
 * @author noear 2025/2/16 created
 */
public class ActionRxHandler implements RxHandler {
    private Action action;
    private Publisher publisher;
    private boolean isStreaming;

    public ActionRxHandler(Action action, Publisher publisher, boolean isStreaming) {
        this.action = action;
        this.publisher = publisher;
        this.isStreaming = isStreaming;
    }


    @Override
    public Completable handle(RxContext ctx) {
        return Completable.create(emitter -> {
            publisher.subscribe(new ActionRxSubscriber(ctx.toContext(), action, isStreaming, emitter));
        });
    }
}
