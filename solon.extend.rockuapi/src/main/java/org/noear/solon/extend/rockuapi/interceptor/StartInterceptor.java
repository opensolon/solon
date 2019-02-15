package org.noear.solon.extend.rockuapi.interceptor;

import noear.water.utils.Timecount;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/** 开始计时拦截器（用于计时开始） */
public class StartInterceptor implements XHandler {
    @Override
    public void handle(XContext context) throws Exception {
        //开始计时
        context.attrSet("timecount", new Timecount().start());
    }
}
