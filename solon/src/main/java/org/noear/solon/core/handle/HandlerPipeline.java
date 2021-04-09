package org.noear.solon.core.handle;

import java.util.LinkedList;
import java.util.List;

/**
 * 处理管道，提供处理链的存储
 *
 * @author noear
 * @since 1.0
 */
public class HandlerPipeline implements Handler {
    private List<Handler> chain = new LinkedList<>();

    public HandlerPipeline next(Handler handler) {
        chain.add(handler);
        return this;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        for (Handler h : chain) {
            if (ctx.getHandled()) {
                break;
            }

            h.handle(ctx);
        }
    }
}
