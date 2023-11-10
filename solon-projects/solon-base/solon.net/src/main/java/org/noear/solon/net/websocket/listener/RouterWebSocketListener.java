package org.noear.solon.net.websocket.listener;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.route.RoutingDefault;
import org.noear.solon.core.route.RoutingTable;
import org.noear.solon.core.route.RoutingTableDefault;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author noear
 * @since 2.5
 */
public class RouterWebSocketListener implements WebSocketListener {
    private final RoutingTable<WebSocketListener> routesL;

    public RouterWebSocketListener() {
        routesL = new RoutingTableDefault<>();
    }

    public int count() {
        return routesL.count();
    }

    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param index    顺序位
     * @param listener 监听接口
     */
    public void of(String path, int index, WebSocketListener listener) {
        WebSocketListener lh = new ExpressWebSocketListener(path, listener);

        routesL.add(new RoutingDefault<>(path, MethodType.SOCKETD, index, lh));
    }

    /**
     * 添加路由关系 for Listener
     *
     * @param path     路径
     * @param listener 监听接口
     */
    public void of(String path, WebSocketListener listener) {
        of(path, 0, listener);
    }

    /**
     * 移除路由关系 for Listener
     *
     * @param path 路径
     */
    public void remove(String path) {
        routesL.remove(path);
    }

    /**
     * 区配一个目标（根据上下文）
     *
     * @param s 会话对象
     * @return 首个匹配监听
     */
    public WebSocketListener matchOne(WebSocket s) {
        String path = s.getPath();

        if (path == null) {
            return null;
        } else {
            return routesL.matchOne(path, MethodType.SOCKETD); //WEBSOCKET 取消了， 借用一下
        }
    }

    public Collection<WebSocketListener> matchMore(WebSocket s) {
        String path = s.getPath();

        if (path == null) {
            return null;
        } else {
            return routesL.matchMore(path, MethodType.SOCKETD);
        }
    }

    @Override
    public void onOpen(WebSocket s) {
        Collection<WebSocketListener> listeners = matchMore(s);
        if (listeners != null) {
            for (WebSocketListener l1 : listeners) {
                l1.onOpen(s);
            }
        }
    }

    @Override
    public void onMessage(WebSocket s, String text) throws IOException {
        Collection<WebSocketListener> listeners = matchMore(s);
        if (listeners != null) {
            for (WebSocketListener l1 : listeners) {
                l1.onMessage(s, text);
            }
        }
    }

    @Override
    public void onMessage(WebSocket s, ByteBuffer binary) throws IOException {
        Collection<WebSocketListener> listeners = matchMore(s);
        if (listeners != null) {
            for (WebSocketListener l1 : listeners) {
                l1.onMessage(s, binary);
            }
        }
    }

    @Override
    public void onClose(WebSocket s) {
        Collection<WebSocketListener> listeners = matchMore(s);
        if (listeners != null) {
            for (WebSocketListener l1 : listeners) {
                l1.onClose(s);
            }
        }
    }

    @Override
    public void onError(WebSocket s, Throwable error) {
        Collection<WebSocketListener> listeners = matchMore(s);
        if (listeners != null) {
            for (WebSocketListener l1 : listeners) {
                l1.onError(s, error);
            }
        }
    }
}
