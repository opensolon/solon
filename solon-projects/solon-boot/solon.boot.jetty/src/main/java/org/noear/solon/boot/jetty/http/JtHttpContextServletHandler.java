package org.noear.solon.boot.jetty.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

public class JtHttpContextServletHandler extends SolonServletHandler {
    static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Override
    protected void preHandle(Context ctx) {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        if (FORM_URLENCODED.equals(ctx.contentType())) {
            ctx.paramMap();
        }
    }
}
