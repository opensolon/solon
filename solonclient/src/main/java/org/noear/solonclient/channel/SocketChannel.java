package org.noear.solonclient.channel;

import org.noear.solonclient.IChannel;
import org.noear.solonclient.Result;
import org.noear.solonclient.XProxy;

import java.util.Map;

public class SocketChannel implements IChannel {
    public static final SocketChannel instance = new SocketChannel();

    @Override
    public Result call(XProxy proxy, Map<String, String> headers, Map<String, Object> args) throws Exception {
        SocketMessage msg = SocketUtils.send(proxy.url(), proxy.serializer().stringify(args));

        return new Result(msg.charset, msg.content);
    }
}
