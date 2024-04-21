package org.noear.solon.net.websocket;

import org.noear.solon.Solon;
import org.noear.solon.net.websocket.listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.listener.PathWebSocketListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSoskcet 路由器
 *
 * @author noear
 * @since 2.6
 */
public class WebSocketRouter {
    private final PipelineWebSocketListener rootListener = new PipelineWebSocketListener();
    private final PathWebSocketListener pathListener = new PathWebSocketListener(true);
    private final Set<String> paths = new HashSet<>();

    private WebSocketRouter() {
        rootListener.next(pathListener);
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
     * 前置监听，如果没有同类型的
     */
    public void beforeIfAbsent(WebSocketListener listener) {
        rootListener.prevIfAbsent(listener);
    }

    /**
     * 主监听
     */
    public void of(String path, WebSocketListener listener) {
        pathListener.of(path, listener);
        paths.add(path);
    }

    /**
     * 后置监听
     */
    public void after(WebSocketListener listener) {
        rootListener.next(listener);
    }

    /**
     * 后置监听，如果没有同类型的
     */
    public void afterIfAbsent(WebSocketListener listener) {
        rootListener.nextIfAbsent(listener);
    }

    public WebSocketListener getListener() {
        return rootListener;
    }

    public Collection<String> getPaths() {
        return Collections.unmodifiableSet(paths);
    }
}
