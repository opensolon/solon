package org.noear.solon.core;

import org.noear.solon.XRouter;

/**
 * XApp Handler
 * */
public class XRouterHandler implements XHandler {
    private XRouter<XHandler> _router;
    public XRouterHandler(XRouter<XHandler> router){
        _router = router;
    }


    @Override
    public void handle(XContext context) throws Exception {
        //可能上级链已完成处理
        if (context.getHandled()) {
            return;
        }

        boolean _handled = false;

        //前置处理
        do_try_handle(context, XEndpoint.before);

        //主体处理
        if (context.getHandled() == false) {
            _handled = do_try_handle(context, XEndpoint.main);
        }

        //后置处理
        do_try_handle(context, XEndpoint.after); //前后不能反 （后置处理由内部进行状态控制）

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

    protected boolean do_try_handle(XContext context, int endpoint) throws Exception {
        XHandler handler = _router.matchOne(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return true;
        } else {
            return false;
        }
    }
}
