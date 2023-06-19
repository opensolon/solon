package org.noear.solon.web.reactive;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Action 响应式返回处理
 *
 * @author noear
 * @since 2.3
 */
public class ActionReturnReactiveHandler implements ActionReturnHandler {
    @Override
    public void handle(Context ctx, Action action, Object result) throws Throwable {
        ActionReactiveSubscriber subscriber = new ActionReactiveSubscriber(ctx, action);

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
