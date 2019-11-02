package org.noear.solon.core;

import org.noear.solon.XRouter;

/**
 * XApp Handler
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
        do_try_handle_multiple(context, XEndpoint.before);

        //主体处理
        if (context.getHandled() == false) {
            //（仅支持唯一代理）
            _handled = do_try_handle_one(context, XEndpoint.main);
        }

        //后置处理（支持多代理）
        do_try_handle_multiple(context, XEndpoint.after); //前后不能反 （后置处理由内部进行状态控制）

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

    protected boolean do_try_handle_one(XContext context, int endpoint) throws Throwable {
        XHandler handler = _router.matchOne(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return context.status() != 404;
        } else {
            return false;
        }
    }

    protected void do_try_handle_multiple(XContext context, int endpoint) throws Throwable {
        for(XHandler handler: _router.matchAll(context,endpoint)){
            handler.handle(context);
        }
    }
}
