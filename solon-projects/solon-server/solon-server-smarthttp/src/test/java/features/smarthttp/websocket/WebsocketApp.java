package features.smarthttp.websocket;

import features.smarthttp.ServerText;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * 测试websocket的虚拟线程适配
 * 请在jdk21下运行
 *
 * @author jaime
 * @version 1.0
 * @since 2025/1/21
 */
@Controller
public class WebsocketApp {
    public static void main(String[] args) {
        Solon.start(ServerText.class, args, app -> app.enableWebSocket(true));
    }

    @Mapping("hello")
    public String hello() {
        System.out.println("当前线程是否是虚拟线程: " + ThreadUtil.isVirtualThread());
        return "hello";
    }

}
