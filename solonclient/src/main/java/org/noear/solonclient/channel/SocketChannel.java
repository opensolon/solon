package org.noear.solonclient.channel;

import org.noear.solonclient.IChannel;
import org.noear.solonclient.XProxy;

import java.util.Map;

public class SocketChannel implements IChannel {
    public static final SocketChannel instance = new SocketChannel();

    @Override
    public String call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception {
        SocketMessage msg = SocketUtils.send(proxy.url(),proxy.serializer().stringify(args));
        return msg.toString();
    }
}
