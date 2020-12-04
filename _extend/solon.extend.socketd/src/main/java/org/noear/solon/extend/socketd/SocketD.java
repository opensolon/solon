package org.noear.solon.extend.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.channel.socketd.SocketChannel;
import org.noear.nami.decoder.SnackDecoder;
import org.noear.nami.encoder.SnackTypeEncoder;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.message.Session;

import java.net.URI;

public class SocketD {
    //
    // session client
    //
    public static Session create(URI uri, boolean autoReconnect) {
        return SessionFactoryManager.create(uri, autoReconnect);
    }

    public static Session create(URI uri) {
        return create(uri, true);
    }

    public static Session create(String uri, boolean autoReconnect) {
        return create(URI.create(uri), autoReconnect);
    }

    public static Session create(String uri) {
        return create(uri, true);
    }


    //
    // rpc client
    //

    public static <T> T create(URI uri, Class<T> service) {
        Session session = create(uri, true);
        return create(session, service);
    }

    public static <T> T create(String uri, Class<T> service) {
        Session session = create(uri,true);
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
