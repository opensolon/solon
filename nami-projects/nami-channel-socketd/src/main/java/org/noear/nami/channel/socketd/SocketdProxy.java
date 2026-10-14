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
package org.noear.nami.channel.socketd;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.socketd.handle.ToHandlerListener;

/**
 * Socketd 代理
 *
 * @author noear
 * @since 2.6
 */
public class SocketdProxy {
    public static final ToHandlerListener socketdToHandler = new ToHandlerListener();

    /**
     * 创建接口代理
     */
    public static <T> T create(String url, Class<T> clz) throws Exception {
        ClientSession session;
        if (url.contains(",")) {
            session = SocketD.createClusterClient(url.split(","))
                    .listen(socketdToHandler)
                    .openOrThow();
        } else {
            session = SocketD.createClient(url)
                    .listen(socketdToHandler)
                    .openOrThow();
        }

        return ProxyUtils.create(() -> session, null, null, clz);
    }

    /**
     * 创建接口代理
     */
    public static <T> T create(ClientSession session, Class<T> clz) {
        return ProxyUtils.create(() -> session, null, null, clz);
    }

    /**
     * 创建接口代理
     */
    public static <T> T create(Context ctx, Class<T> clz) {
        if (ctx.response() instanceof Session) {
            return create((Session) ctx.response(), clz);
        } else {
            return create((Session) ctx.request(), clz);
        }
    }
}