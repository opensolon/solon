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
    public RouterHandler(Router router){
        bind(router);
    }

    /**
     * 绑定路由器
     * */
    public void bind(Router router){
        this.router = router;
    }


    @Override
    public void handle(Context context) throws Throwable {
        //可能上级链已完成处理
        if (context.getHandled()) {
            return;
        }

        boolean _handled = false;

        //前置处理（支持多代理）
        handleMultiple(context, Endpoint.before);

        //主体处理
        if (context.getHandled() == false) {
            //（仅支持唯一代理）
            _handled = handleOne(context, Endpoint.main);
        }

        //后置处理（支持多代理）
        handleMultiple(context, Endpoint.after); //前后不能反 （后置处理由内部进行状态控制）

        //汇总状态
        if (_handled) {
            if (context.status() < 1) {
                context.statusSet(200);
            }
            context.setHandled(true);
        }

        if (context.status() < 1) {
            context.statusSet(404);
        }
    }

    /**
     * 唯一处理（用于主处理）
     * */
    protected boolean handleOne(Context context, Endpoint endpoint) throws Throwable {
        Handler handler = router.matchOne(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return context.status() != 404;
        } else {
            return false;
        }
    }

    /**
     * 多项目处理（用于拦截器）
     * */
    protected void handleMultiple(Context context, Endpoint endpoint) throws Throwable {
        for(Handler handler: router.matchAll(context,endpoint)){
            handler.handle(context);
        }
    }
}
