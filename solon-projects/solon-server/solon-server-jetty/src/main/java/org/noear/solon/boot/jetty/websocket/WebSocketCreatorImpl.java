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
package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.DecodeUtils;
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
        String path = DecodeUtils.rinseUri(servletUpgradeRequest.getRequestURI().getPath());
        SubProtocolCapable subProtocolCapable = webSocketRouter.getSubProtocol(path);
        if (subProtocolCapable != null) {
            String protocols = subProtocolCapable.getSubProtocols(servletUpgradeRequest.getSubProtocols());

            if (Utils.isNotEmpty(protocols)) {
                servletUpgradeResponse.setAcceptedSubProtocol(protocols);
            }
        }

        return new WebSocketListenerImpl();
    }
}