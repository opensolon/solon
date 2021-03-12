package org.noear.nami.channel.socketd;

import org.noear.nami.NamiChannel;
import org.noear.nami.NamiConfig;
import org.noear.nami.common.Result;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionFlag;
import org.noear.solon.socketd.SocketD;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/1 created
 */
public class SocketClientChannel extends SocketChannelFilter implements NamiChannel {
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
    public Result call(NamiConfig cfg, Method method, String action, String url, Map<String, String> headers, Map<String, Object> args, Object body) throws Throwable {
        URI uri = URI.create(url);
        SocketChannel channel = get(uri);

        return channel.call(cfg, method, action, url, headers, args, body);
    }
}
