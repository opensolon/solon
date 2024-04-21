package org.noear.solon.net.websocket.listener;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.5
 */
public class PathWebSocketListener implements WebSocketListener {
    static final Logger log = LoggerFactory.getLogger(PathWebSocketListener.class);

    private final RoutingTable<WebSocketListener> routingTable;
    private final boolean autoClose;

    public PathWebSocketListener() {
       this(false);
    }

    public PathWebSocketListener(boolean autoClose) {
        this.routingTable = new RoutingTableDefault<>();
        this.autoClose = autoClose;
    }

    public int count() {
        return routingTable.count();
    }

    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param index    顺序位
     * @param listener 监听接口
     */
    public PathWebSocketListener of(String path, int index, WebSocketListener listener) {
        WebSocketListener lh = new ExpressWebSocketListener(path, listener);

        routingTable.add(new RoutingDefault<>(path, MethodType.SOCKET, index, lh));
        return this;
    }

    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param listener 监听接口
     */
    public PathWebSocketListener of(String path, WebSocketListener listener) {
        return of(path, 0, listener);
    }

    /**
     * 移除路由关系 for Listener
     *
     * @param path 路径
     */
    public PathWebSocketListener remove(String path) {
        routingTable.remove(path);
        return this;
    }

    /**
     * 区配一个目标
     */
    protected WebSocketListener matching(WebSocket s) {
        String path = s.path();

        if (path == null) {
            return null;
        } else {
            return routingTable.matchOne(path, MethodType.SOCKET); //WEBSOCKET 取消了， 借用一下
        }
    }

    @Override
    public void onOpen(WebSocket s) {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onOpen(s);
        } else if (autoClose) {
            s.close();
            log.warn("Route failed. The connection will close. id={}", s.id());
        }
    }

    @Override
    public void onMessage(WebSocket s, String text) throws IOException {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onMessage(s, text);
        }
    }

    @Override
    public void onMessage(WebSocket s, ByteBuffer binary) throws IOException {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onMessage(s, binary);
        }
    }

    @Override
    public void onClose(WebSocket s) {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onClose(s);
        }
    }

    @Override
    public void onError(WebSocket s, Throwable error) {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onError(s, error);
        }
    }

    @Override
    public void onPing(WebSocket s) {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onPing(s);
        }
    }

    @Override
    public void onPong(WebSocket s) {
        WebSocketListener l1 = matching(s);
        if (l1 != null) {
            l1.onPong(s);
        }
    }
}