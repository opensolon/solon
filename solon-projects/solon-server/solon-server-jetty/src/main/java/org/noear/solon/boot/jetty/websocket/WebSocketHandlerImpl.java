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

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class WebSocketHandlerImpl extends WebSocketHandler {

    @Override
    public void configure(WebSocketServletFactory factory) {
        //由监听器内个性定制
        //factory.getPolicy().setIdleTimeout(10L * 60L * 1000L);
        //factory.getPolicy().setAsyncWriteTimeout(10L * 1000L);

        //注册览听器
        //factory.register(WebSocketListenerImpl.class);

        //设置生成器（握手、子协议、监听器）
        factory.setCreator(new WebSocketCreatorImpl());
    }
}

