package demo;

import org.noear.solon.Solon;

/**
 * stomp server 测试
 *
 * @author noear
 * @since 2.4
 */
public class StompServerTest {

    public static void main(String[] args) {
        Solon.start(StompServerTest.class, args, app -> {
            app.enableWebSocket(true);
        });
    }

}
