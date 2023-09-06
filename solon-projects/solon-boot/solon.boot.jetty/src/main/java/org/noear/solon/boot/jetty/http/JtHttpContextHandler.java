package org.noear.solon.boot.jetty.http;

import org.noear.solon.Solon;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.XPluginImp;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.servlet.SolonServletContext;
import org.noear.solon.core.event.EventBus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

public class JtHttpContextHandler extends AbstractHandler {
    static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            handleDo(baseRequest, request, response);

        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            EventBus.publishTry(e);

            response.setStatus(500);
        }
    }

    private void handleDo(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws Exception{
        SolonServletContext ctx = new SolonServletContext(request, response);
        ctx.attrSet("signal", XPluginImp.signal());

        ctx.contentType("text/plain;charset=UTF-8");
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        //预处理 FORM_URLENCODED
        pretreatment(ctx);
        Solon.app().tryHandle(ctx);

        if (ctx.getHandled() || ctx.status() >= 200) {
            baseRequest.setHandled(true);
        } else {
            response.setStatus(404);
            baseRequest.setHandled(true);
        }
    }

    /**
     * 预处理
     * */
    private void pretreatment(Context ctx) throws Exception {
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
