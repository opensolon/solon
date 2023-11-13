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

/**
 * Socketd 客户端通道
 *
 * @author noear
 * @since 1.3
 * @since 2.6
 */
public class SocketdClientChannel extends ChannelBase implements Channel {
    public static final SocketdClientChannel instance = new SocketdClientChannel();

    Map<String, SocketdChannel> channelMap = new HashMap<>();

    private SocketdChannel get(URI uri) {
        String hostname = uri.getAuthority();
        SocketdChannel channel = channelMap.get(hostname);

        if (channel == null) {
            synchronized (hostname.intern()) {
                channel = channelMap.get(hostname);

                if (channel == null) {
                    try {
                        Session session = SocketD.createClient(uri.toString())
                                .listen(SocketdProxy.socketdToHandler)
                                .open();
                        channel = new SocketdChannel(() -> session);
                        channelMap.put(hostname, channel);
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

        return channel;
    }

    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        URI uri = URI.create(ctx.url);
        SocketdChannel channel = get(uri);

        return channel.call(ctx);
    }
}
