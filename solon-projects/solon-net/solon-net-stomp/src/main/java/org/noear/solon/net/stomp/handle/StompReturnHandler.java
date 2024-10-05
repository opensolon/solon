package org.noear.solon.net.stomp.handle;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.ActionReturnHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.annotation.To;

/**
 * @author noear 2024/10/4 created
 */
public class StompReturnHandler implements ActionReturnHandler {
    static StompReturnHandler instance = new StompReturnHandler();

    public static StompReturnHandler getInstance() {
        return instance;
    }

    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return false;
    }

    @Override
    public void returnHandle(Context ctx, Action action, Object returnValue) throws Throwable {
        if (returnValue != null) {

            To anno = action.method().getAnnotation(To.class);
            StompContext ctx1 = (StompContext) ctx;

            String payload;

            if (returnValue instanceof String) {
                payload = (String) returnValue;
            } else {
                payload = ctx1.renderAndReturn(returnValue);
            }

            if (anno == null) {
                ctx1.getMessageSender().sendTo(ctx1.getSession(), payload);
            } else {
                for (String destination : anno.value()) {
                    ctx1.getMessageSender().sendTo(destination, payload);
                }
            }
        }
    }
}