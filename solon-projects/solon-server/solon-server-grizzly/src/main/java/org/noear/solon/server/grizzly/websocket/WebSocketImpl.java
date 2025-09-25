package org.noear.solon.server.grizzly.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.noear.solon.Utils;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.noear.solon.server.util.DecodeUtils;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 *
 * @author noear 2025/9/25 created
 *
 */
public class WebSocketImpl extends WebSocketTimeoutBase {
    private final DefaultWebSocket real;
    private final HttpServletRequest req;

    public WebSocketImpl(DefaultWebSocket real) {
        this.real = real;
        this.req = real.getUpgradeRequest();

        String uri = buildUri(req);
        this.init(URI.create(uri));
    }

    public String buildUri(HttpServletRequest req) {
        String url = req.getRequestURL().toString();
        if (Utils.isEmpty(req.getQueryString())) {
            return DecodeUtils.rinseUri(url);
        } else {
            if (url.contains("?")) {
                return DecodeUtils.rinseUri(url);
            } else {
                return DecodeUtils.rinseUri(url) + "?" + req.getQueryString();
            }
        }
    }

    @Override
    public boolean isValid() {
        return isClosed() == false;
    }

    @Override
    public boolean isSecure() {
        return req.isSecure();
    }

    private InetSocketAddress remoteAddress;

    @Override
    public InetSocketAddress remoteAddress() {
        if (remoteAddress == null) {
            remoteAddress = new InetSocketAddress(req.getRemoteHost(), req.getRemotePort());
        }
        return remoteAddress;
    }

    private InetSocketAddress localAddress;

    @Override
    public InetSocketAddress localAddress() {
        if (localAddress == null) {
            localAddress = new InetSocketAddress(req.getLocalAddr(), req.getLocalPort());
        }
        return localAddress;
    }

    @Override
    public Future<Void> send(String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            real.send(text);
            onSend();

            future.complete(null);
        } catch (Throwable ex) {
            future.completeExceptionally(ex);
        }

        return future;
    }

    @Override
    public Future<Void> send(ByteBuffer binary) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            real.send(binary.array());

            onSend();
            future.complete(null);
        } catch (Throwable ex) {
            future.completeExceptionally(ex);
        }

        return future;
    }

    @Override
    public void close() {
        super.close();
        RunUtil.runAndTry(real::close);
    }
}
