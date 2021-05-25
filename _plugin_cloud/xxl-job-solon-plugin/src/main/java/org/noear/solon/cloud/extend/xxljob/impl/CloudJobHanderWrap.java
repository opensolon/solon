package org.noear.solon.cloud.extend.xxljob.impl;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobHanderWrap extends IJobHandler {
    Handler real;

    public CloudJobHanderWrap(Handler handler) {
        real = handler;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        Context ctx = Context.current();
        try {
            if (ctx == null) {
                ctx = new ContextEmpty();
                try {
                    ContextUtil.currentSet(ctx);

                    ctx.paramMap().put("param",param);
                    real.handle(ctx);
                } finally {
                    ContextUtil.currentSet(ctx);
                }
            } else {
                ctx.paramMap().put("param",param);
                real.handle(ctx);
            }

            return ReturnT.SUCCESS;
        } catch (Throwable e) {
            return ReturnT.FAIL;
        }
    }
}
