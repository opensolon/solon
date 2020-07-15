package org.noear.solon.core;

import org.noear.solon.XRouter;

/**
 * XApp router Handler
 * */
public class XRouterHandler implements XHandler {
    private XRouter _router;
    public XRouterHandler(XRouter router){
        _router = router;
    }


    @Override
    public void handle(XContext context) throws Throwable {
        //可能上级链已完成处理
        if (context.getHandled()) {
            return;
        }

        boolean _handled = false;

        //前置处理（支持多代理）
        handleMultiple(context, XEndpoint.before);

        //主体处理
        if (context.getHandled() == false) {
            //（仅支持唯一代理）
            _handled = handleOne(context, XEndpoint.main);
        }

        //后置处理（支持多代理）
        handleMultiple(context, XEndpoint.after); //前后不能反 （后置处理由内部进行状态控制）

        //汇总状态
        if (_handled) {
            if (context.status() < 1) {
                context.status(200);
            }
            context.setHandled(true);
        }

        if (context.status() < 1) {
            context.status(404);
        }
    }

    /**
     * 唯一处理（用于主处理）
     * */
    protected boolean handleOne(XContext context, int endpoint) throws Throwable {
        XHandler handler = _router.matchOne(context, endpoint);

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
    protected void handleMultiple(XContext context, int endpoint) throws Throwable {
        for(XHandler handler: _router.matchAll(context,endpoint)){
            handler.handle(context);
        }
    }
}
