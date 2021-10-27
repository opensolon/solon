package org.noear.solon.core.route;

import org.noear.solon.core.event.EventBus;
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
    public void handle(Context ctx) throws Throwable {
        //可能上级链已完成处理
        if (ctx.getHandled()) {
            return;
        }

        boolean _handled = false;
        boolean _throwabled = false;

        try {
            //前置处理（支持多代理）
            handleMultiple(ctx, Endpoint.before);

            //主体处理
            if (ctx.getHandled() == false) {
                //（仅支持唯一代理）
                _handled = handleOne(ctx, Endpoint.main);
                //（设定处理状态，便于 after 获取状态）
                ctx.setHandled(_handled);
            }
        } catch (Throwable e) {
            _throwabled = true;
            if (ctx.errors == null) {
                ctx.errors = e; //如果内部已经做了，就不需要了
                EventBus.push(e);
            }
            throw e;
        } finally {
            //后置处理（支持多代理）
            handleMultiple(ctx, Endpoint.after); //前后不能反 （后置处理由内部进行状态控制）

            //汇总状态
            if (_throwabled == false) {
                if (ctx.status() < 1) {
                    if (_handled) {
                        ctx.status(200);
                    } else {
                        ctx.status(404);
                    }
                }
            }
        }
    }

    /**
     * 唯一处理（用于主处理）
     */
    protected boolean handleOne(Context ctx, Endpoint endpoint) throws Throwable {
        Handler h = router.matchOne(ctx, endpoint);

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
