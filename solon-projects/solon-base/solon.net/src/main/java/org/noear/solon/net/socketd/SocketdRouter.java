package org.noear.solon.net.socketd;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.solon.Solon;
import org.noear.solon.net.socketd.listener.RouterListenerPlus;

/**
 * WebSoskcet 路由器
 *
 * @author noear
 * @since 2.6
 */
public class SocketdRouter {
    private final PipelineListener rootListener = new PipelineListener();
    private final RouterListenerPlus routerListener = new RouterListenerPlus();

    private SocketdRouter() {
        rootListener.next(routerListener);
    }

    public static SocketdRouter getInstance() {
        return Solon.context().attachmentOf(SocketdRouter.class, SocketdRouter::new);
    }

    /**
     * 前置监听
     */
    public void before(Listener listener) {
        rootListener.prev(listener);
    }

    /**
     * 主监听
     */
    public void main(String path,  Listener listener) {
        routerListener.of(path, listener);
    }

    /**
     * 后置监听
     */
    public void after(Listener listener) {
        rootListener.next(listener);
    }

    public Listener getListener() {
        return rootListener;
    }
}
