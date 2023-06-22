package org.noear.solon.web.reactive;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import org.reactivestreams.Publisher;

/**
 * Action 响应式返回处理
 *
 * @author noear
 * @since 2.3
 */
public class ActionReturnReactiveHandler implements ActionReturnHandler {
    @Override
    public boolean matched(Class<?> returnType) {
        return Publisher.class.isAssignableFrom(returnType);
    }

    @Override
    public void returnHandle(Context ctx, Action action, Object result) throws Throwable {
        if (result != null) {
            ActionReactiveSubscriber subscriber = new ActionReactiveSubscriber(ctx, action);

            if (ctx.asyncSupported()) {
                ctx.asyncStart(0L, null);
            }

            ((Publisher) result).subscribe(subscriber);
        }
    }
}
