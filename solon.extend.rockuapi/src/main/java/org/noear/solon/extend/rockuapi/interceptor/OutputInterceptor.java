package org.noear.solon.extend.rockuapi.interceptor;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/** 输出拦截器（用于内容格式化并输出） */
public class OutputInterceptor implements XHandler {

    @Override
    public void handle(XContext context) throws Exception {
        String output = context.attr("output", null);

        if (output != null) {
            context.outputAsJson(output);
        }
    }
}
