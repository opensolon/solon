package org.noear.solon.socketd.client.jdksocket;

import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;

import java.net.Socket;
import java.net.SocketException;

public class BioReceiver {
    /**
     * 接收数据
     */
    public static Message receive(Socket socket) {
        try {
            return BioProtocol.instance.decode(socket.getInputStream());
        } catch (SocketException ex) {
            return null;
        } catch (Throwable ex) {
            EventBus.push(ex);
            return null;
        }
    }
}
