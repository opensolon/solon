package org.noear.solon.extend.uapi;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * UAPI默认渲染器
 * */
public class UApiRender implements XHandler {
    @Override
    public void handle(XContext c) throws Throwable {
        Object result = c.attr("result", null);

        if (result != null) {
            c.render(result);
        }
    }
}
