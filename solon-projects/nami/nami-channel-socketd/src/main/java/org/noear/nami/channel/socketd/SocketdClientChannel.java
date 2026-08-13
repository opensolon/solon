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

import org.noear.nami.Channel;
import org.noear.nami.ChannelBase;
import org.noear.nami.Context;
import org.noear.nami.Result;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socketd 客户端通道
 *
 * @author noear
 * @since 1.3
 * @since 2.6
 */
public class SocketdClientChannel extends ChannelBase implements Channel {
    public static final SocketdClientChannel instance = new SocketdClientChannel();

    // 修复：改用并发容器，避免无锁读与并发写的数据竞争
    private final Map<String, SocketdChannel> channelMap = new ConcurrentHashMap<>();

    private SocketdChannel get(String hostname, String url) {
        // 修复：用 computeIfAbsent 原子地获取或建立通道，失败时不缓存，下次可重试
        return channelMap.computeIfAbsent(hostname, k -> {
            try {
                Session session = (Session) SocketD.createClient(url)
                        .listen(SocketdProxy.socketdToHandler)
                        .openOrThow();
                return new SocketdChannel(() -> session);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        URI uri = URI.create(ctx.url);
        String hostname = uri.getAuthority();
        SocketdChannel channel = get(hostname, ctx.url);

        try {
            return channel.call(ctx);
        } catch (Throwable ex) {
            // 修复：调用失败（如会话已断开）时移除缓存条目，下次调用触发重建，避免永久不可用
            channelMap.remove(hostname, channel);
            throw ex;
        }
    }
}
