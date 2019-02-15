package org.noear.solon.extend.rockuapi.interceptor;

import org.noear.solon.extend.rockuapi.encoder.RockDefEncoder;
import org.noear.solon.extend.rockuapi.encoder.RockEncoder;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;
import org.noear.solon.extend.rockuapi.RockApiParams;

/** 输出签名拦截器（用于输出内容的签名） */
public class OutputSignInterceptor implements XHandler {
    private RockEncoder _encoder;

    public OutputSignInterceptor(RockEncoder encoder) {
        if(encoder == null)
            _encoder = new RockDefEncoder();
        else
            _encoder = encoder;
    }

    @Override
    public void handle(XContext context) throws Exception {
        String output = context.attr("output", null);

        if (output != null) {
            RockApiParams params = context.attr("params", null);

            String out_sign = _encoder.tryEncode(context, params.getApp(), output);

            context.headerSet("sign", out_sign);
        }
    }
}
