package org.noear.nami.channel.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.common.Constants;
import org.noear.solon.core.message.Session;

import java.net.URI;
import java.util.function.Supplier;

/**
 * 代理工具
 *
 * @author noear
 * @since 1.10
 */
public class ProxyUtils {
    /**
     * 创建接口
     * */
    public static <T> T create(Supplier<Session> sessions, Encoder encoder, Decoder decoder, Class<T> service) {
        URI uri = sessions.get().uri();
        if (uri == null) {
            uri = URI.create("tcp://socketd");
        }

        String server = uri.getScheme() + ":" + uri.getSchemeSpecificPart();

        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .headerSet(Constants.HEADER_ACCEPT, Constants.CONTENT_TYPE_JSON) //相当于指定默认解码器 //如果指定不同的编码器，会被盖掉
                .headerSet(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON) //相当于指定默认编码器
                .channel(new SocketChannel(sessions))
                .upstream(() -> server)
                .create(service);
    }
}
