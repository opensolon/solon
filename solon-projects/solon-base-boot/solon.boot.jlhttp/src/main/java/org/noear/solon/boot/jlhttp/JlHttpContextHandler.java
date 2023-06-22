package org.noear.solon.boot.jlhttp;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.Handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {
    private final Handler handler;
    public JlHttpContextHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        try {
            return handleDo(request, response);
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            EventBus.pushTry(e);

            response.sendHeaders(500);
            return 0;
        }
    }

    protected int handleDo(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
        JlHttpContext ctx = new JlHttpContext(request, response);

        try {
            ctx.contentType("text/plain;charset=UTF-8");

            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
            }

            handler.handle(ctx);

            if(ctx.innerIsAsync()) {
                //如果启用了异步?
                ctx.asyncAwait();
            }

            if (ctx.getHandled() || ctx.status() >= 200) {
                ctx.commit();
                return 0;
            } else {
                return 404;
            }
        }catch (Throwable e){
            EventBus.pushTry(e);

            return 500;
        }
    }
}
