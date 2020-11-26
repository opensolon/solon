package org.noear.fairy.channel.xsocket;

import org.noear.fairy.Fairy;
import org.noear.fairy.Decoder;
import org.noear.fairy.Encoder;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;

public class XSocket {
    public static <T> T create(String host, int port, Class<T> service) {
        Session session = SessionFactory.create(host, port, true);
        return create(session, service);
    }

    public static <T> T create(Session session, Class<T> service) {
        return create(session, SnackEncoder.instance, SnackDecoder.instance, service);
    }

    public static <T> T create(Session session, Encoder encoder, Decoder decoder, Class<T> service) {
        SocketChannel channel = new SocketChannel(() -> session);

        return Fairy.builder()
                .encoder(encoder)
                .decoder(decoder)
                .upstream(() -> "tpc://xsocket")
                .channel(channel)
                .create(service);
    }
}
