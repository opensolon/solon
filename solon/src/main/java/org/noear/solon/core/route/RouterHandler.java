package org.noear.solon.core.route;

import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * Solon router Handler
 *
 * @author noear
 * @since 1.0
 * */
public class RouterHandler implements Handler {
    private Router router;

    public RouterHandler(Router router) {
        bind(router);
    }

    /**
     * 绑定路由器
     */
    public void bind(Router router) {
        this.router = router;
    }


    @Override
    public void handle(Context x) throws Throwable {
        //可能上级链已完成处理
        if (x.getHandled()) {
            return;
        }

        Handler mainHandler = router.matchMain(x);

        try {
            //预处理 action
            if (mainHandler instanceof Action) {
                x.attrSet("action", mainHandler);
            }

            //前置处理（支持多代理）
            handleMultiple(x, Endpoint.before);

            //主体处理
            if (x.getHandled() == false) {
                //（仅支持唯一代理）
                //（设定处理状态，便于 after 获取状态）
                x.setHandled(handleMain(mainHandler, x));
            }
        } catch (Throwable e) {
            if (x.errors == null) {
                x.errors = e; //如果内部已经做了，就不需要了
            }
            throw e;
        } finally {
            //后置处理（支持多代理）
            handleMultiple(x, Endpoint.after); //前后不能反 （后置处理由内部进行状态控制）
        }
    }

    /**
     * 唯一处理（用于主处理）
     */
    protected boolean handleMain(Handler h,Context ctx) throws Throwable {
        if (h != null) {
            h.handle(ctx);
            return ctx.status() != 404;
        } else {
            return false;
        }
    }

    /**
     * 多项目处理（用于拦截器）
     */
    protected void handleMultiple(Context ctx, Endpoint endpoint) throws Throwable {
        for (Handler h : router.matchAll(ctx, endpoint)) {
            h.handle(ctx);
        }
    }
}
