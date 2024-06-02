package org.noear.solon.core.route;

import org.noear.solon.Solon;
import org.noear.solon.core.Constants;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.lang.Nullable;

/**
 * Solon router Handler
 *
 * @author noear
 * @since 1.0
 * */
public class RouterHandler implements Handler, RouterInterceptor {
    private Router router;

    public RouterHandler(Router router) {
        this.router = router;
    }

    //拦截实现
    @Override
    public void doIntercept(Context ctx, @Nullable Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        handleDo(ctx, mainHandler);
    }


    /**
     * 主处理
     */
    protected boolean handleMain(Handler h, Context ctx) throws Throwable {
        if (h != null) {
            h.handle(ctx);
            return ctx.status() != 404;
        } else {
            int code = ctx.attrOrDefault(Constants.mainStatus, 404);
            if (code == 405) {
                throw new StatusException("Method Not Allowed: " + ctx.method() + " " + ctx.pathNew(), code);
            } else {
                throw new StatusException("Not Found: " + ctx.pathNew(), code);
            }
            //return false;
        }
    }

    /**
     * 触发器处理（链式拦截）
     */
    protected void handleTriggers(Context ctx, Endpoint endpoint) throws Throwable {
        for (Handler h : router.matchMore(ctx, endpoint)) {
            h.handle(ctx);
        }
    }


    private void handleDo(Context x, Handler mainHandler) throws Throwable {
        //可能上级链已完成处理
        if (x.getHandled()) {
            return;
        }

        try {
            //前置触发器（支持多处理）
            handleTriggers(x, Endpoint.before);

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
            //后置触发器（支持多处理）
            handleTriggers(x, Endpoint.after); //前后不能反 （后置处理由内部进行状态控制）
        }
    }

    @Override
    public void handle(Context x) throws Throwable {
        //可能上级链已完成处理
        if (x.getHandled()) {
            return;
        }

        //提前获取主处理
        Handler mainHandler = router.matchMain(x);

        //预处理 action
        if (mainHandler instanceof Action) {
            x.attrSet(Constants.action, mainHandler);
        }

        //执行
        Solon.app().chainManager().doIntercept(x, mainHandler);
    }
}
