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
 * @author noear
 * @since 3.7.3
 */
public class TcWebSocketManager {
    private static final Logger log = LoggerFactory.getLogger(TcWebSocketManager.class);
    
    private TcWebSocketManager() {}
    
    private static boolean enableWebSocket = false;
    
    /**
     * 在Tomcat上下文中初始化WebSocket支持
     * 注意：此方法需要在上下文初始化之前调用
     */
    public static void init(Context context) {
        try {
            // 注册WebSocket容器初始化器
            context.addServletContainerInitializer(new WsSci(), null);
            enableWebSocket = true;
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
        try {
        	if (context == null) {
                log.error("Tomcat Context is null, cannot register WebSocket endpoints");
                return;
            }
            // 获取ServerContainer
            ServerContainer serverContainer = (ServerContainer) context.getServletContext()
                    .getAttribute("javax.websocket.server.ServerContainer");
            
            if (serverContainer == null) {
                throw new IllegalStateException("Missing javax.websocket.server.ServerContainer");
            }

            // 获取所有注册的路径
            Collection<String> paths = WebSocketRouter.getInstance().getPaths();

            for (String path : paths) {
                if (path.startsWith("/")) {
                    ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder
                            .create(TcWebSocketEndpoint.class, path)
                            .build();
                    serverContainer.addEndpoint(endpointConfig);
                    log.info("Tomcat Registered WebSocket endpoint: {}", path);
                }
            }
        } catch (Throwable e) {
            log.error("Failed to register WebSocket endpoints", e);
            throw new RuntimeException(e);
        }
    }

	public static boolean isEnableWebSocket() {
		return enableWebSocket;
	}
    
}