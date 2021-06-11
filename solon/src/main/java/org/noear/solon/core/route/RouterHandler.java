package org.noear.solon.core.route;

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
    private Handler def;
    public RouterHandler(Router router){
        bind(router);
    }

    /**
     * 绑定路由器
     * */
    public void bind(Router router){
        this.router = router;
    }

    public void bind(Handler def){
        this.def = def;
    }


    @Override
    public void handle(Context ctx) throws Throwable {
        //可能上级链已完成处理
        if (ctx.getHandled()) {
            return;
        }

        boolean _handled = false;

        //前置处理（支持多代理）
        handleMultiple(ctx, Endpoint.before);

        //主体处理
        if (ctx.getHandled() == false) {
            //（仅支持唯一代理）
            _handled = handleOne(ctx, Endpoint.main);
        }

        //后置处理（支持多代理）
        handleMultiple(ctx, Endpoint.after); //前后不能反 （后置处理由内部进行状态控制）

        //汇总状态
        if (_handled) {
            if (ctx.status() < 1) {
                ctx.statusSet(200);
            }
            ctx.setHandled(true);
        }

        if (ctx.status() < 1) {
            ctx.statusSet(404);
        }
    }

    /**
     * 唯一处理（用于主处理）
     * */
    protected boolean handleOne(Context ctx, Endpoint endpoint) throws Throwable {
        Handler handler = router.matchOne(ctx, endpoint);

        if (handler != null) {
            handler.handle(ctx);
            return ctx.status() != 404;
        } else {
            if (def == null) {
                return false;
            } else {
                def.handle(ctx);
                return ctx.status() != 404;
            }
        }
    }

    /**
     * 多项目处理（用于拦截器）
     * */
    protected void handleMultiple(Context ctx, Endpoint endpoint) throws Throwable {
        for(Handler handler: router.matchAll(ctx,endpoint)){
            handler.handle(ctx);
        }
    }
}
