package org.noear.solon.cloud.extend.xxljob.service;

import com.xxl.job.core.handler.IJobHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
class CloudJobHanderProxy extends IJobHandler {
    Handler real;

    public CloudJobHanderProxy(Handler handler) {
        real = handler;
    }

    @Override
    public void execute() throws Exception {
        Context ctx = Context.current();

        try {
            if (ctx == null) {
                ctx = new ContextEmpty();
                try {
                    ContextUtil.currentSet(ctx);
                    real.handle(ctx);
                } finally {
                    ContextUtil.currentSet(ctx);
                }
            } else {
                real.handle(ctx);
            }
        } catch (Throwable e) {
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}