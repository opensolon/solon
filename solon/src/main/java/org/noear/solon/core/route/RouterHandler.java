package org.noear.solon.core.route;

import org.noear.solon.core.handle.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Solon router Handler
 *
 * @author noear
 * @since 1.0
 * */
public class RouterHandler implements Handler, RouterInterceptor {
    private Router router;
    //for handler interceptorList
    private final List<RouterInterceptorEntity> interceptorList;

    public RouterHandler(Router router) {
        bind(router);
        interceptorList = new ArrayList<>();
        addInterceptor(this, Integer.MAX_VALUE);
    }

    public void addInterceptor(RouterInterceptor interceptor, int index) {
        RouterInterceptorEntity node = new RouterInterceptorEntity(index, interceptor);
        interceptorList.add(node);
        interceptorList.sort(Comparator.comparingInt(f -> f.index));
    }

    /**
     * 绑定路由器
     */
    public void bind(Router router) {
        this.router = router;
    }


    private void handleDo(Context x) throws Throwable {
        //可能上级链已完成处理
        if (x.getHandled()) {
            return;
        }

        Handler mainHandler = x.attr("_MainHandler");

        try {
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
    protected boolean handleMain(Handler h, Context ctx) throws Throwable {
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

    @Override
    public void doIntercept(Context ctx, RouterInterceptorChain chain) throws Throwable {
        handleDo(ctx);
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
            x.attrSet("action", mainHandler);
        }

        //执行
        new RouterInterceptorChainImpl(interceptorList).doIntercept(x);
    }
}
