package demo;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear 2021/11/10 created
 */
@CloudEvent("demo.user.created")
public class EventHandlerDemo1 implements CloudEventHandler {
    @Override
    public boolean handler(Event event) throws Throwable {
        //送2块金币
        return true;
    }
}
