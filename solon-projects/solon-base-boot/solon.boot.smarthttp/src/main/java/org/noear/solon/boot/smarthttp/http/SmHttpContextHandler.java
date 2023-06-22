package org.noear.solon.boot.smarthttp.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.smarthttp.XPluginImp;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ContextAsyncListener;
import org.noear.solon.core.handle.Handler;
import org.smartboot.http.common.enums.HttpStatus;
import org.smartboot.http.server.HttpRequest;
import org.smartboot.http.server.HttpResponse;
import org.smartboot.http.server.HttpServerHandler;
import org.smartboot.http.server.impl.Request;
import org.smartboot.socket.util.AttachKey;
import org.smartboot.socket.util.Attachment;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public class SmHttpContextHandler extends HttpServerHandler {
    static final AttachKey httpHolderKey = AttachKey.valueOf("httpHolder");

    protected Executor executor;
    private final Handler handler;

    public SmHttpContextHandler(Handler handler) {
        this.handler = handler;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onClose(Request request) {
        if (request.getAttachment() == null) {
            return;
        }

        SmHttpContext ctx = (SmHttpContext) request.getAttachment().get(httpHolderKey);
        if (ctx != null && ctx.innerIsAsync()) {
            for (ContextAsyncListener listener : ctx.innerAsyncListeners()) {
                try {
                    listener.onComplete(ctx);
                } catch (Throwable e) {
                    EventBus.pushTry(e);
                }
            }
        }
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, CompletableFuture<Object> future) throws IOException {
        SmHttpContext ctx = new SmHttpContext(request, response, future);
        if (request.getAttachment() == null) {
            request.setAttachment(new Attachment());
        }
        request.getAttachment().put(httpHolderKey, ctx);


        if (executor == null) {
            handle0(ctx, future);
        } else {
            try {
                executor.execute(() -> {
                    handle0(ctx, future);
                });
            } catch (RejectedExecutionException e) {
                handle0(ctx, future);
            }
        }
    }

    protected void handle0(SmHttpContext ctx, CompletableFuture<Object> future) {
        try {
            handleDo(ctx);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (ctx.innerIsAsync() == false) {
                future.complete(ctx);
            }
        }
    }

    protected void handleDo(SmHttpContext ctx) {
        try {
            if ("PRI".equals(ctx.method())) {
                ctx.innerGetResponse().setHttpStatus(HttpStatus.NOT_IMPLEMENTED);
                return;
            }

            ctx.contentType("text/plain;charset=UTF-8");
            if (ServerProps.output_meta) {
                ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
            }

            handler.handle(ctx);

            if (ctx.innerIsAsync() == false) {
                ctx.innerCommit();
            }

        } catch (Throwable e) {
            EventBus.pushTry(e);

            ctx.innerGetResponse().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}