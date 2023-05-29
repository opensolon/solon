package demo;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.noear.solon.Solon;

/**
 * @author noear 2023/3/23 created
 */
public class Http2 {
    public static void main(String[] args) {
        Solon.start(Http2.class, args, app -> {
            app.onEvent(Undertow.Builder.class, e -> {
                //启用 http2
                e.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                //再加个 http 监听（server.port 被 https 占了）//如果不需要，则不加
                e.addHttpListener(8081, "0.0.0.0");
            });
        });
    }
}
