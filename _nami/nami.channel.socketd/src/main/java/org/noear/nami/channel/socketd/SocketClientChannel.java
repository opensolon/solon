package org.noear.nami.channel.socketd;

import org.noear.nami.NamiChannel;
import org.noear.nami.NamiContext;
import org.noear.nami.common.Result;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionFlag;
import org.noear.solon.socketd.SocketD;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/1 created
 */
public class SocketClientChannel implements NamiChannel {
    public static final SocketClientChannel instance = new SocketClientChannel();

    Map<String, SocketChannel> channelMap = new HashMap<>();

    private SocketChannel get(URI uri) {
        String hostname = uri.getAuthority();
        SocketChannel channel = channelMap.get(hostname);

        if (channel == null) {
            synchronized (hostname.intern()) {
                channel = channelMap.get(hostname);

                if (channel == null) {
                    Session session = SocketD.createSession(uri);
                    session.flagSet(SessionFlag.socketd);
                    channel = new SocketChannel(() -> session);
                    channelMap.put(hostname, channel);
                }
            }
        }

        return channel;
    }

    @Override
    public Result call(NamiContext ctx) throws Throwable {
        URI uri = URI.create(ctx.url);
        SocketChannel channel = get(uri);

        return channel.call(ctx);
    }


}
