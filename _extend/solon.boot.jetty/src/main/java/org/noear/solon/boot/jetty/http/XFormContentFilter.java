package org.noear.solon.boot.jetty.http;


import org.noear.solon.XUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

import java.net.URLDecoder;

/**
 * 为jetty DELETE,PATCH进行表单转码
 *
 * @author noear
 * @since 1.0
 * */
public class XFormContentFilter implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        String method = ctx.method();

        if ("DELETE".equals(method) || "PATCH".equals(method)) {
            parseBodyTry(ctx);
        }
    }

    private void parseBodyTry(XContext ctx) throws Exception {
        String ct = ctx.contentType();

        if (ct == null || ctx.paramMap().size() > 0) {
            return;
        }

        if (ct.startsWith("application/x-www-form-urlencoded") == false) {
            return;
        }

        if (XUtil.isEmpty(ctx.body())) {
            return;
        }

        String[] ss = ctx.body().split("&");

        for (String s1 : ss) {
            String[] ss2 = s1.split("=");

            if (ss2.length == 2) {
                ctx.paramMap().put(ss2[0], URLDecoder.decode(ss2[1], "utf-8"));
            }
        }
    }
}
