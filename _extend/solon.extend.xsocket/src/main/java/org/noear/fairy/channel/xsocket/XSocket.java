package org.noear.fairy.channel.xsocket;

import org.noear.fairy.Fairy;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;

public class XSocket {
    public static <T> T get(String host, int port, Class<T> service) {
        Session session = SessionFactory.create(host, port, true);
        SocketChannel channel = new SocketChannel(() -> session);

        return Fairy.builder()
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> "tpc://xsocket")
                .channel(channel)
                .create(service);
    }

    public static <T> T get(Session session, Class<T> service) {
        SocketChannel channel = new SocketChannel(() -> session);

        return Fairy.builder()
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> "tpc://xsocket")
                .channel(channel)
                .create(service);
    }
}
