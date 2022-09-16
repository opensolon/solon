package org.noear.solon.core.handle;

import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.ListenerPipeline;

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

    /**
     * 下一步
     * */
    public HandlerPipeline next(Handler handler) {
        chain.add(handler);
        return this;
    }

    /**
     * 上一步
     * */
    public HandlerPipeline prev(Handler handler) {
        chain.add(0, handler);
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
