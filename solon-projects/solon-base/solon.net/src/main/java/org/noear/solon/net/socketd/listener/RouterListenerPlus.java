package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.BuilderListener;
import org.noear.socketd.transport.core.listener.RouterListener;

/**
 * 路由监听器增强版
 *
 * @author noear
 * @since 2.6
 */
public class RouterListenerPlus extends RouterListener {
    public RouterListenerPlus() {
        super(new RouterExpress());
    }

    /**
     * 路由
     */
    @Override
    public RouterListener of(String path, Listener listener) {
        router.add(path, new ExpressListener(path, listener));
        return this;
    }

    /**
     * 路由
     */
    @Override
    public BuilderListener of(String path) {
        BuilderListener listener = new BuilderListener();
        router.add(path, new ExpressListener(path, listener));
        return listener;
    }
}
