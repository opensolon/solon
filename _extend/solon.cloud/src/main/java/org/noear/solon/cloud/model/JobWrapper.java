package org.noear.solon.cloud.model;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 工作包装器
 *
 * @author noear
 * @since 1.6
 */
public class JobWrapper implements CloudJobHandler {
    Handler handler;

    public JobWrapper(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        handler.handle(ctx);
    }
}
