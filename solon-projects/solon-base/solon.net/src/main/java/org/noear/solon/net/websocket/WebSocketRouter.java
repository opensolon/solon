package org.noear.solon.net.websocket;

import org.noear.solon.Solon;
import org.noear.solon.net.websocket.listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.listener.RouterWebSocketListener;

/**
 * WebSoskcet 路由器
 *
 * @author noear
 * @since 2.6
 */
public class WebSocketRouter {
    private final PipelineWebSocketListener rootListener = new PipelineWebSocketListener();
    private final RouterWebSocketListener routerListener = new RouterWebSocketListener();

    private WebSocketRouter() {
        rootListener.next(routerListener);
    }

    public static WebSocketRouter getInstance() {
        //方便在单测环境下切换 SolonApp，可以相互独立
        return Solon.context().attachmentOf(WebSocketRouter.class, WebSocketRouter::new);
    }

    /**
     * 前置监听
     */
    public void before(WebSocketListener listener) {
        rootListener.prev(listener);
    }

    /**
     * 主监听
     */
    public void of(String path, WebSocketListener listener) {
        routerListener.of(path, listener);
    }

    /**
     * 后置监听
     */
    public void after(WebSocketListener listener) {
        rootListener.next(listener);
    }

    public WebSocketListener getListener() {
        return rootListener;
    }
}
