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

import org.noear.solon.core.util.PathMatcher;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 路径表过式监听器（自动解析出 /user/{id} 的值）
 *
 * @author noear
 * @since 2.0
 */
public class ExpressWebSocketListener implements WebSocketListener {
    private WebSocketListener listener;

    //path 分析器
    private PathMatcher pathAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;

    public ExpressWebSocketListener(String path, WebSocketListener listener) {
        this.listener = listener;

        if (path != null && path.indexOf("{") >= 0) {
            path = PathUtil.mergePath(null, path);

            pathKeys = new ArrayList<>();
            Matcher pm = PathUtil.pathKeyExpr.matcher(path);
            while (pm.find()) {
                pathKeys.add(pm.group(1));
            }

            if (pathKeys.size() > 0) {
                pathAnalyzer = PathMatcher.get(path);
            }
        }
    }

    public WebSocketListener getListener() {
        return listener;
    }

    @Override
    public void onOpen(WebSocket s) {
        //获取path var
        if (pathAnalyzer != null) {
            Matcher pm = pathAnalyzer.matcher(s.path());
            if (pm.find()) {
                for (int i = 0, len = pathKeys.size(); i < len; i++) {
                    s.param(pathKeys.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                }
            }
        }

        listener.onOpen(s);
    }

    @Override
    public void onMessage(WebSocket s, String text) throws IOException {
        listener.onMessage(s, text);
    }

    @Override
    public void onMessage(WebSocket s, ByteBuffer binary) throws IOException {
        listener.onMessage(s, binary);
    }

    @Override
    public void onClose(WebSocket s) {
        listener.onClose(s);
    }

    @Override
    public void onError(WebSocket s, Throwable e) {
        listener.onError(s, e);
    }

    @Override
    public void onPing(WebSocket s) {
        listener.onPing(s);
    }

    @Override
    public void onPong(WebSocket s) {
        listener.onPong(s);
    }
}
