/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.noear.solon.net.websocket.WebSocketListenerSupplier;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.noear.solon.net.websocket.listener.ContextPathWebSocketListener;

/**
 * @author noear
 * @since 2.6
 */
public class NetPlugin implements Plugin {

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
        String path = Solon.cfg().getByTmpl(anno.value()); //支持属性配置
        boolean registered = false;


        if (bw.raw() instanceof WebSocketListenerSupplier) {
            //websocket //第一优先
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            WebSocketListenerSupplier supplier = bw.raw();
            webSocketRouter.of(path, supplier.getWebSocketListener());
            registered = true;
        } else if (bw.raw() instanceof WebSocketListener) {
            //websocket //第二优先
            if (Utils.isEmpty(path)) {
                path = "**";
            }

            webSocketRouter.of(path, bw.raw());
            registered = true;
        } else if (ClassUtil.hasClass(() -> Listener.class)) {
            //socket.d //第三优先
            if (bw.raw() instanceof Listener) {
                if (Utils.isEmpty(path)) {
                    path = "**";
                }

                socketdRouter.of(path, bw.raw());
                registered = true;
            }
        }

        if (registered == false) {
            LogUtil.global().warn("@ServerEndpoint does not support type: " + clz.getName());
        }
    }
}