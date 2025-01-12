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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Socketd 客户端通道
 *
 * @author noear
 * @since 1.3
 * @since 2.6
 */
public class SocketdClientChannel extends ChannelBase implements Channel {
    public static final SocketdClientChannel instance = new SocketdClientChannel();

    private final Map<String, SocketdChannel> channelMap = new HashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();

    private SocketdChannel get(String hostname, String url) {
        SocketdChannel channel = channelMap.get(hostname);

        if (channel == null) {
            SYNC_LOCK.lock();
            try {
                channel = channelMap.get(hostname);

                if (channel == null) {
                    try {
                        Session session = (Session) SocketD.createClient(url)
                                .listen(SocketdProxy.socketdToHandler)
                                .openOrThow();
                        channel = new SocketdChannel(() -> session);
                        channelMap.put(hostname, channel);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return channel;
    }

    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        URI uri = URI.create(ctx.url);
        String hostname = uri.getAuthority();
        SocketdChannel channel = get(hostname, ctx.url);

        return channel.call(ctx);
    }
}
