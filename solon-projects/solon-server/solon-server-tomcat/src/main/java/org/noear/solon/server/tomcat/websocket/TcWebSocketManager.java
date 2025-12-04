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
package org.noear.solon.server.tomcat.websocket;

import java.util.Collection;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.catalina.Context;
import org.apache.tomcat.websocket.server.WsSci;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tomcat WebSocket 管理器
 * 
 * @author 小xu中年
 * @since 3.7.3
 */
public class TcWebSocketManager {
    private static final Logger log = LoggerFactory.getLogger(TcWebSocketManager.class);

    private TcWebSocketManager() {
    }

    /**
     * 在Tomcat上下文中初始化WebSocket支持
     * 注意：此方法需要在上下文初始化之前调用
     */
    public static void init(Context context) {
        if (context == null) {
            log.error("Tomcat Context is null, cannot initialize WebSocket");
            return;
        }

        try {
            // 注册WebSocket容器初始化器
            context.addServletContainerInitializer(new WsSci(), null);
            log.info("Tomcat WebSocket initialized");
        } catch (Exception e) {
            log.error("Failed to initialize Tomcat WebSocket", e);
        }
    }

    /**
     * 注册WebSocket端点
     * 注意：此方法需要在服务器启动后调用
     */
    public static void registerEndpoints(Context context) {
        if (context == null) {
            log.error("Tomcat Context is null, cannot register WebSocket endpoints");
            return;
        }

        try {
            // 获取ServerContainer
            ServerContainer serverContainer = (ServerContainer) context.getServletContext()
                    .getAttribute("javax.websocket.server.ServerContainer");

            if (serverContainer == null) {
                log.error("Tomcat Context Missing javax.websocket.server.ServerContainer");
                return;
            }

            // 获取所有注册的路径
            Collection<String> paths = WebSocketRouter.getInstance().getPaths();

            for (String path : paths) {
                if (path.startsWith("/")) {
                    // 使用自定义配置器创建端点配置
                    ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder
                            .create(TcWebSocketEndpoint.class, path)
                            .configurator(new TcWebSocketConfigurator())
                            .build();

                    serverContainer.addEndpoint(endpointConfig);
                    log.info("Tomcat Registered WebSocket endpoint: {}", path);
                }
            }
        } catch (Throwable e) {
            log.error("Failed to register Tomcat WebSocket endpoints", e);
        }
    }
}