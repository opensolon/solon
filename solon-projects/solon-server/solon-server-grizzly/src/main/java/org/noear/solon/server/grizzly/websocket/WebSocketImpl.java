package org.noear.solon.server.grizzly.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.noear.solon.Utils;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;
import org.noear.solon.server.util.DecodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);
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

        if (real.isConnected()) {
            try {
                real.close();
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }
    }

    @Override
    public void close(int code, String reason) {
        super.close(code, reason);

        if (real.isConnected()) {
            try {
                real.close(code, reason);
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }
    }
}
