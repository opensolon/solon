package org.noear.solon.net.integration;

import org.noear.socketd.transport.core.Listener;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.socketd.SocketdRouter;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.noear.solon.net.websocket.listener.ContextPathWebSocketListener;

/**
 * @author noear
 * @since 2.6
 */
public class XPluginImpl implements Plugin {

    private SocketdRouter socketdRouter;
    private WebSocketRouter webSocketRouter;

    @Override
    public void start(AppContext context) throws Throwable {
        webSocketRouter = WebSocketRouter.getInstance();

        //websocket
        context.lifecycle((() -> {
            //尝试设置 context-path
            if (Utils.isNotEmpty(Solon.cfg().serverContextPath())) {
                webSocketRouter.beforeIfAbsent(new ContextPathWebSocketListener());
            }
        }));

        //socket.d
        if (ClassUtil.hasClass(() -> Listener.class)) {
            socketdRouter = SocketdRouter.getInstance();
        }

        //添加注解处理
        context.beanBuilderAdd(ServerEndpoint.class, this::serverEndpointBuild);
    }

    private void serverEndpointBuild(Class<?> clz, BeanWrap bw, ServerEndpoint anno) {
        String path = anno.value();
        boolean registered = false;

        //socket.d
        if (ClassUtil.hasClass(() -> Listener.class)) {
            if (bw.raw() instanceof Listener) {
                if (Utils.isEmpty(path)) {
                    path = "**";
                }

                socketdRouter.of(path, bw.raw());
                registered = true;
            }
        }

        //websocket
        if (bw.raw() instanceof WebSocketListener) {
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            webSocketRouter.of(path, bw.raw());
            registered = true;
        }

        if (registered == false) {
            LogUtil.global().warn("@ServerEndpoint does not support type: " + clz.getName());
        }
    }
}