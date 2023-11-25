package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.core.listener.PathListener;

/**
 * 路由监听器增强版
 *
 * @author noear
 * @since 2.6
 */
public class PathListenerPlus extends PathListener {
    public PathListenerPlus() {
        super(new PathMapperExpress());
    }

    /**
     * 路由
     */
    @Override
    public PathListener of(String path, Listener listener) {
        mapper.put(path, new ExpressListener(path, listener));
        return this;
    }

    /**
     * 路由
     */
    @Override
    public EventListener of(String path) {
        EventListener listener = new EventListener();
        mapper.put(path, new ExpressListener(path, listener));
        return listener;
    }
}
