package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;

/**
 * WebSocket 生成器
 *
 * @author noear
 * @since 2.8
 */
public class WebSocketCreatorImpl implements WebSocketCreator {
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest, ServletUpgradeResponse servletUpgradeResponse) {
        //添加子协议支持
        String path = servletUpgradeRequest.getRequestURI().getPath();
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            servletUpgradeResponse.setAcceptedSubProtocol(subProtocolCapable.getSubProtocols());
        }

        return new WebSocketListenerImpl();
    }
}