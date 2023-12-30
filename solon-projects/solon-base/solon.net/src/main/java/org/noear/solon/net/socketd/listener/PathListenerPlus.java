package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.PathListener;

/**
 * 路径监听器增强版
 *
 * @author noear
 * @since 2.6
 */
public class PathListenerPlus extends PathListener {
    public PathListenerPlus() {
        super(new RouteSelectorExpress());
    }

    /**
     * 路由
     */
    @Override
    public PathListener of(String path, Listener listener) {
        pathRouteSelector.put(path, new ExpressListener(path, listener));
        return this;
    }
}
