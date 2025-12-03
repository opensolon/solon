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

import java.util.List;
import java.util.Map;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.noear.solon.Utils;
import org.noear.solon.net.websocket.SubProtocolCapable;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.noear.solon.server.util.DecodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tomcat WebSocket 配置器
 * 
 * @author 小xu中年
 * @since 3.7.3
 */
public class TcWebSocketConfigurator extends ServerEndpointConfig.Configurator {
    private static final Logger log = LoggerFactory.getLogger(TcWebSocketConfigurator.class);
    
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 存储握手请求信息，供后续使用
        sec.getUserProperties().put("handshakeRequest", request);
        
        // 处理子协议协商
        String path = DecodeUtils.rinseUri(request.getRequestURI().getPath());
        SubProtocolCapable subProtocolCapable = WebSocketRouter.getInstance().getSubProtocol(path);
        
        if (subProtocolCapable != null) {
            List<String> clientProtocols = request.getHeaders().get("Sec-WebSocket-Protocol");
            if (clientProtocols != null && !clientProtocols.isEmpty()) {
                String protocols = subProtocolCapable.getSubProtocols(clientProtocols);
                
                if (Utils.isNotEmpty(protocols)) {
                    response.getHeaders().put("Sec-WebSocket-Protocol", List.of(protocols));
                }
            }
        }
        
        // 存储请求头信息
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                sec.getUserProperties().put("header." + entry.getKey(), entry.getValue().get(0));
            }
        }
    }
    
    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        try {
            return endpointClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Tomcat WebSocket Failed to create endpoint instance: {}", endpointClass.getName(), e);
            throw new InstantiationException("Tomcat WebSocket Failed to create endpoint instance: " + e.getMessage());
        }
    }
}