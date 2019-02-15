package org.noear.solon.extend.rockuapi.interceptor;

import noear.water.WaterClient;
import noear.water.utils.Timecount;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.wateradpter.XWaterAdapter;

/** 结束计时拦截器（完成计时，并发送到WATER） */
public class EndInterceptor implements XHandler {
    private String _service;
    private String _tag;
    public EndInterceptor(String service, String tag){
        _service = service;
        _tag = tag;
    }

    @Override
    public void handle(XContext context) throws Exception {
        /** 获取一下计时器（开始计时的时候设置的） */
        Timecount timecount = context.attr("timecount", null);

        if (timecount == null) {
            return;
        }

        String path = context.path();
        String node = XWaterAdapter.global().localHost();

        WaterClient.Registry.track(_service, _tag, path, timecount.stop().milliseconds(), node);
    }
}
