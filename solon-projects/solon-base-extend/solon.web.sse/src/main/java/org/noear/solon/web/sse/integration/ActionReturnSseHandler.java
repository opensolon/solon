package org.noear.solon.web.sse.integration;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.sse.SseEmitter;
import org.noear.solon.web.sse.SseEmitterHandler;

/**
 * @author noear
 * @since 2.3
 */
public class ActionReturnSseHandler implements ActionReturnHandler {
    @Override
    public boolean matched(Class<?> returnType) {
        return SseEmitter.class.isAssignableFrom(returnType);
    }

    @Override
    public void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable {
        if (returnValue != null) {
            new SseEmitterHandler((SseEmitter) returnValue).start();
        }
    }
}
