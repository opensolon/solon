package org.noear.solon.boot.jetty.http;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.web.servlet.SolonServletHandler;
import org.noear.solon.core.handle.Context;

import java.io.IOException;
import java.net.URLDecoder;

public class JtHttpContextServletHandler extends SolonServletHandler {
    static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        pretreatment(ctx);
    }

    /**
     * 预处理
     */
    private void pretreatment(Context ctx) throws IOException {
        if (FORM_URLENCODED.equals(ctx.contentType())) {
            ctx.paramMap();

            String method = ctx.method();
            if ("DELETE".equals(method) || "PATCH".equals(method) || "PUT".equals(method)) {
                if (ctx.paramMap().size() > 0) {
                    return;
                }

                if (Utils.isEmpty(ctx.bodyNew())) {
                    return;
                }

                String[] ss = ctx.bodyNew().split("&");

                for (String s1 : ss) {
                    String[] ss2 = s1.split("=");

                    if (ss2.length == 2) {
                        ctx.paramMap().put(ss2[0], URLDecoder.decode(ss2[1], ServerProps.request_encoding));
                    }
                }
            }
        }
    }
}
