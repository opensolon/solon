package org.noear.solon.web.reactor;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author noear
 * @since 2.3
 */
public class ActionReturnReactiveHandler implements ActionReturnHandler {
    @Override
    public void handle(Context ctx, Action action, Object result) throws Throwable {
        SubscriberImpl subscriber = new SubscriberImpl(ctx, action);

        if (result instanceof Flux) {
            ((Flux) result).subscribe(subscriber);
            return;
        }

        if (result instanceof Mono) {
            ((Mono) result).subscribe(subscriber);
            return;
        }
    }
}
