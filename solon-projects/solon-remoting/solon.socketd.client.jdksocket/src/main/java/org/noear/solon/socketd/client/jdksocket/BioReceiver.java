package org.noear.solon.socketd.client.jdksocket;

import org.noear.solon.core.message.Message;

import java.io.IOException;
import java.net.Socket;

public class BioReceiver {
    /**
     * 接收数据
     */
    public static Message receive(Socket socket) throws IOException {
        return BioProtocol.instance.decode(socket.getInputStream());
    }
}
