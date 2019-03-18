package org.noear.solon.core;

import org.noear.solon.XApp;

/**
 * XApp Handler
 * */
public class XAppHandler implements XHandler {
    private XApp _app;
    public XAppHandler(XApp app){
        _app = app;
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

    private boolean do_try_handle(XContext context, int endpoint) throws Exception {
        XHandler handler = _app.matched(context, endpoint);

        if (handler != null) {
            handler.handle(context);
            return true;
        } else {
            return false;
        }
    }
}
