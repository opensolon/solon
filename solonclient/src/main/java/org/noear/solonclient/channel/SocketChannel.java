package org.noear.solonclient.channel;

import org.noear.solonclient.IChannel;
import org.noear.solonclient.Result;
import org.noear.solonclient.XProxyConfig;

import java.util.Map;

public class SocketChannel implements IChannel {
    public static final SocketChannel instance = new SocketChannel();

    @Override
    public Result call(XProxyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args) throws Exception {
        SocketMessage msg = SocketUtils.send(url, (String) cfg.serializer.serialize(args));

        return new Result(msg.charset, msg.content);
    }
}
