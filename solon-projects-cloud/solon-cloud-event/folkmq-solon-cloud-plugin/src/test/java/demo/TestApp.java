package demo;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 2.6
 */
@CloudEvent("demo")
public class TestApp implements CloudEventHandler {
    public static void main(String[] args) {
        Solon.start(TestApp.class, args);

        CloudClient.event().publish(new Event("demo", "demo!!!!!!"));
    }

    @Override
    public boolean handle(Event event) throws Throwable {
        System.out.println("打印：" + event.content());
        return true;
    }
}
