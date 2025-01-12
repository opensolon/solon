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
package org.noear.solon.net.websocket.listener;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供 ContextPath 类似的功能（优先级要极高）
 *
 * @author noear
 * @since 2.5
 */

/**
 * 提供 ContextPath 类似的功能（优先级要极高）
 *
 * @author noear
 * @since 2.5
 */
public class ContextPathWebSocketListener extends SimpleWebSocketListener {
    private static final Logger log = LoggerFactory.getLogger(ContextPathWebSocketListener.class);

    private final String contextPath0;
    private final String contextPath1;
    private final boolean forced;

    public ContextPathWebSocketListener() {
        this(Solon.cfg().serverContextPath(), Solon.cfg().serverContextPathForced());
    }

    /**
     * @param contextPath '/demo/'
     */
    public ContextPathWebSocketListener(String contextPath, boolean forced) {
        this.forced = forced;

        if (Utils.isEmpty(contextPath)) {
            contextPath0 = null;
            contextPath1 = null;
        } else {
            String newPath = null;
            if (contextPath.endsWith("/")) {
                newPath = contextPath;
            } else {
                newPath = contextPath + "/";
            }

            if (newPath.startsWith("/")) {
                this.contextPath1 = newPath;
            } else {
                this.contextPath1 = "/" + newPath;
            }

            this.contextPath0 = contextPath1.substring(0, contextPath1.length() - 1);
        }
    }

    @Override
    public void onOpen(WebSocket s) {
        if (contextPath0 != null) {
            if (s.path().equals(contextPath0)) {
                //www:888 加 abc 后，仍可以用 www:888/abc 打开
                s.pathNew("/");
            } else if (s.path().startsWith(contextPath1)) {
                s.pathNew(s.path().substring(contextPath1.length() - 1));
            } else {
                if (forced) {
                    try {
                        s.close();
                    } catch (Exception e) {
                        log.warn("ContextPathListener onOpen failed!", e);
                    }
                }
            }
        }
    }
}
