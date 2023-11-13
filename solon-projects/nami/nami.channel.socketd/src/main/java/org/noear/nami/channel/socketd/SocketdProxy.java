package org.noear.nami.channel.socketd;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.handle.Context;
import org.noear.solon.net.socketd.handle.ToHandlerListener;

/**
 * Socketd 代理
 *
 * @author noear
 * @since 2.6
 */
public class SocketdProxy {
    public static final ToHandlerListener socketdToHandler = new ToHandlerListener();

    /**
     * 创建接口代理
     */
    public static <T> T create(String url, Class<T> clz) throws Exception {
        Session session = SocketD.createClient(url).listen(socketdToHandler).open();
        return ProxyUtils.create(() -> session, null, null, clz);
    }

    /**
     * 创建接口代理
     */
    public static <T> T create(Session session, Class<T> clz) {
        return ProxyUtils.create(() -> session, null, null, clz);
    }

    /**
     * 创建接口代理
     */
    public static <T> T create(Context ctx, Class<T> clz) {
        if (ctx.response() instanceof Session) {
            return create((Session) ctx.response(), clz);
        } else {
            return create((Session) ctx.request(), clz);
        }
    }
}
