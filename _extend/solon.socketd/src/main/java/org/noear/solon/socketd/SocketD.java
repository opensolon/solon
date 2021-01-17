package org.noear.solon.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.common.Constants;
import org.noear.nami.channel.socketd.SocketChannel;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.protocol.MessageProtocol;

import java.net.URI;
import java.util.function.Supplier;

/**
 *
 * @author noear
 * @since 1.2
 * */
public class SocketD {
    //
    // protocol
    //
    public static void setProtocol(MessageProtocol protocol) {
        ProtocolManager.setProtocol(protocol);
    }

    //
    // session client
    //
    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(Connector connector) {
        return SessionFactoryManager.create(connector);
    }

    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(URI serverUri, boolean autoReconnect) {
        return SessionFactoryManager.create(serverUri, autoReconnect);
    }

    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(URI serverUri) {
        return createSession(serverUri, true);
    }

    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(String serverUri, boolean autoReconnect) {
        return createSession(URI.create(serverUri), autoReconnect);
    }

    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(String serverUri) {
        return createSession(serverUri, true);
    }


    //
    // rpc client
    //

    public static <T> T create(URI serverUri, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, service);
    }

    public static <T> T create(String serverUri, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, service);
    }

    public static <T> T create(Context context, Class<T> service) {
        if (context.request() instanceof Session) {
            Session session = (Session) context.request();
            return create(() -> session, null, null, service);
        } else {
            throw new IllegalArgumentException("Request context nonsupport socketd");
        }
    }

    public static <T> T create(Session session, Class<T> service) {
        return create(() -> session, service);
    }

    public static <T> T create(Supplier<Session> sessions, Class<T> service) {
        return create(sessions, null, null, service);
    }

    public static <T> T create(Session session, Encoder encoder, Decoder decoder, Class<T> service) {
        return create(() -> session, encoder, decoder, service);
    }

    public static <T> T create(Supplier<Session> sessions, Encoder encoder, Decoder decoder, Class<T> service) {
        URI uri = sessions.get().uri();
        if (uri == null) {
            uri = URI.create("tcp://socketd");
        }

        String server = uri.getScheme() + ":" + uri.getSchemeSpecificPart();

        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .headerSet(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON)
                .headerSet(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
                .channel(new SocketChannel(sessions))
                .upstream(() -> server)
                .create(service);
    }
}
