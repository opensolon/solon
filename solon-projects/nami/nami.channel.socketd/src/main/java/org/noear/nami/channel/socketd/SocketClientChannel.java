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
 * @author noear 2021/1/1 created
 * @since 2.6
 */
public class SocketClientChannel extends ChannelBase implements Channel {
    public static final SocketClientChannel instance = new SocketClientChannel();

    Map<String, SocketChannel> channelMap = new HashMap<>();

    private SocketChannel get(URI uri) {
        String hostname = uri.getAuthority();
        SocketChannel channel = channelMap.get(hostname);

        if (channel == null) {
            synchronized (hostname.intern()) {
                channel = channelMap.get(hostname);

                if (channel == null) {
                    try {
                        Session session = SocketD.createClient(uri.toString()).open();
                        channel = new SocketChannel(() -> session);
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
        SocketChannel channel = get(uri);

        return channel.call(ctx);
    }
}
