package org.noear.solon.boot.jetty.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.boot.web.FormUrlencodedUtils;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

public class JtHttpContextServletHandler extends SolonServletHandler {

    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        //编码窗体预处理
        FormUrlencodedUtils.pretreatment(ctx);
    }
}
