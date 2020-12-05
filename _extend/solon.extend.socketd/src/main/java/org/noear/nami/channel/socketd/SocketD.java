package org.noear.nami.channel.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.decoder.SnackDecoder;
import org.noear.nami.encoder.SnackTypeEncoder;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactoryManager;

import java.net.URI;

public class SocketD {
    //
    // session client
    //
    public static Session create(URI serverUri, boolean autoReconnect) {
        return SessionFactoryManager.create(serverUri, autoReconnect);
    }

    public static Session create(URI serverUri) {
        return create(serverUri, true);
    }

    public static Session create(String serverUri, boolean autoReconnect) {
        return create(URI.create(serverUri), autoReconnect);
    }

    public static Session create(String serverUri) {
        return create(serverUri, true);
    }


    //
    // rpc client
    //

    public static <T> T create(URI serverUri, Class<T> service) {
        Session session = create(serverUri, true);
        return create(session, service);
    }

    public static <T> T create(String serverUri, Class<T> service) {
        Session session = create(serverUri, true);
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

        URI uri = session.uri();
        if (uri == null) {
            uri = URI.create("tcp://socketd");
        }

        String server = uri.getScheme() + ":" + uri.getSchemeSpecificPart();

        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .upstream(() -> server)
                .channel(channel)
                .create(service);
    }
}
