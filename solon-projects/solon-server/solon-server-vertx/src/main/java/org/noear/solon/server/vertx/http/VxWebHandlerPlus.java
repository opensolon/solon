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
package org.noear.solon.server.vertx.http;

import io.vertx.core.http.HttpServerRequest;
import org.noear.solon.Utils;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.vertx.integration.VxHttpPlugin;
import org.noear.solon.core.handle.Context;
import org.noear.solon.server.vertx.websocket.VxWebSocketHandlerImpl;
import org.noear.solon.web.vertx.VxWebHandler;

import java.io.IOException;

/**
 * @author noear
 * @since 2.9
 */
public class VxWebHandlerPlus extends VxWebHandler {
    private boolean enableWebSocket;
    private VxWebSocketHandlerImpl vxWebSocketHandlerImpl = new VxWebSocketHandlerImpl();

    @Override
    public void enableWebSocket(boolean enable) {
        enableWebSocket = enable;
    }

    @Override
    protected void preHandle(Context ctx) throws IOException {
        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Server", VxHttpPlugin.solon_server_ver());
        }
    }

    @Override
    public void handle(HttpServerRequest req) {
        if (enableWebSocket) {
            String upgradeStr = req.getHeader("Upgrade");
            if (Utils.isNotEmpty(upgradeStr)) {
                if (upgradeStr.contains("websocket")) {
                    vxWebSocketHandlerImpl.subProtocolCapable(req);

                    req.toWebSocket().onSuccess(ws -> {
                        vxWebSocketHandlerImpl.handle(ws);
                    });
                    return;
                }
            }
        }

        super.handle(req);
    }
}
