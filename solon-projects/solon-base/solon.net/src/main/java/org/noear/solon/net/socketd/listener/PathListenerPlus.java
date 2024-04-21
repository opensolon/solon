package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.PathListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 路径监听器增强版
 *
 * @author noear
 * @since 2.6
 */
public class PathListenerPlus extends PathListener {
    static final Logger log = LoggerFactory.getLogger(PathListenerPlus.class);

    private final boolean autoClose;

    public PathListenerPlus() {
        this(false);
    }

    public PathListenerPlus(boolean autoClose) {
        super(new RouteSelectorExpress());
        this.autoClose = autoClose;
    }

    @Override
    public void onOpen(Session session) throws IOException {
        Listener l1 = this.pathRouteSelector.select(session.path());
        if (l1 != null) {
            l1.onOpen(session);
        } else if (autoClose) {
            session.close();
            log.warn("Route failed. The connection will close. id={}", session.sessionId());
        }
    }

    /**
     * 路由
     */
    @Override
    public PathListener doOf(String path, Listener listener) {
        pathRouteSelector.put(path, new ExpressListener(path, listener));
        return this;
    }

    @Override
    public EventListener of(String path) {
        EventListener listener = new EventListener();
        pathRouteSelector.put(path, new ExpressListener(path, listener));
        return listener;
    }
}