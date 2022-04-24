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
 * SocketD
 *
 * @author noear
 * @since 1.2
 * */
public class SocketD {
    // protocol //

    /**
     * 设置消息协议
     *
     * @param protocol 协议
     * */
    public static void setProtocol(MessageProtocol protocol) {
        ProtocolManager.setProtocol(protocol);
    }

    // session client //

    /**
     * 创建会话
     *
     * @param connector 链接器
     * */
    public static Session createSession(Connector connector) {
        return SessionFactoryManager.create(connector);
    }

    /**
     * 创建会话
     *
     * @param serverUri 服务端地址
     * @param autoReconnect 是否自动重连
     * */
    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(URI serverUri, boolean autoReconnect) {
        return SessionFactoryManager.create(serverUri, autoReconnect);
    }

    /**
     * 创建会话
     *
     * @param serverUri 服务端地址
     * */
    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(URI serverUri) {
        return createSession(serverUri, true);
    }

    /**
     * 创建会话
     *
     * @param serverUri 服务端地址
     * @param autoReconnect 是否自动重连
     * */
    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(String serverUri, boolean autoReconnect) {
        return createSession(URI.create(serverUri), autoReconnect);
    }

    /**
     * 创建会话
     *
     * @param serverUri 服务端地址
     * */
    @Note("ServerUri 以：ws:// 或 wss:// 或 tcp:// 开头")
    public static Session createSession(String serverUri) {
        return createSession(serverUri, true);
    }


    // rpc client //

    public static <T> T create(URI serverUri, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, service);
    }

    public static <T> T create(URI serverUri, Encoder encoder, Decoder decoder, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, encoder, decoder, service);
    }

    public static <T> T create(String serverUri, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, service);
    }

    public static <T> T create(String serverUri, Encoder encoder, Decoder decoder, Class<T> service) {
        Session session = createSession(serverUri, true);
        return create(() -> session, encoder, decoder, service);
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
                .headerSet(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON) //相当于指定默认解码器
                .headerSet(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON) //相当于指定默认编码器
                .channel(new SocketChannel(sessions))
                .upstream(() -> server)
                .create(service);
    }
}
