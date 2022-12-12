package org.noear.solon.cloud.extend.xxljob.service;

import com.xxl.job.core.handler.IJobHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 * @since 1.11
 */
class IJobHandlerImpl extends IJobHandler {
    Handler real;

    public IJobHandlerImpl(Handler handler) {
        real = handler;
    }

    @Override
    public void execute() throws Exception {
        Context ctx = Context.current(); //可能是从上层代理已生成, v1.11
        if (ctx == null) {
            ctx = new ContextEmpty();
            ContextUtil.currentSet(ctx);
        }

        try {
            real.handle(ctx);
        } catch (Throwable e) {
            if (e instanceof Exception) {
                throw (Exception) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}