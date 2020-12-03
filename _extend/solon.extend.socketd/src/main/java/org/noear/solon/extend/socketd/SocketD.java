package org.noear.solon.extend.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.channel.socketd.SocketChannel;
import org.noear.nami.decoder.SnackDecoder;
import org.noear.nami.encoder.SnackTypeEncoder;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

public class SocketD {

    //
    // rpc client
    //

    public static <T> T create(String host, int port, Class<T> service) {
        Session session = SessionFactory.create(host, port, true);
        return create(session, service);
    }

    public static <T> T create(Context context, Class<T> service) {
        if (context.request() instanceof Session) {
            return create((Session) context.request(), SnackTypeEncoder.instance, SnackDecoder.instance, service);
        } else {
            throw new IllegalArgumentException("Request context nonsupport socketd");
        }
    }

    public static <T> T create(Session session, Class<T> service) {
        return create(session, SnackTypeEncoder.instance, SnackDecoder.instance, service);
    }

    public static <T> T create(Session session, Encoder encoder, Decoder decoder, Class<T> service) {
        SocketChannel channel = new SocketChannel(() -> session);

        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .upstream(() -> "tpc://socketd")
                .channel(channel)
                .create(service);
    }
}
