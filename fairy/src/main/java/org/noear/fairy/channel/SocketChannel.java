package org.noear.fairy.channel;


import org.noear.fairy.IChannel;
import org.noear.fairy.Result;
import org.noear.fairy.FairyConfig;

import java.util.Map;

public class SocketChannel implements IChannel {
    public static final SocketChannel instance = new SocketChannel();

    @Override
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        SocketMessage msg = SocketUtils.send(url, (String) cfg.getEncoder().encode(args));

        return new Result(msg.charset, msg.content);
    }
}
