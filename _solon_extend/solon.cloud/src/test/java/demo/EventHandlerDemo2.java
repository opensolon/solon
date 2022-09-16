package demo;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear 2021/11/10 created
 */
@CloudEvent("demo.user.created")
public class EventHandlerDemo2 implements CloudEventHandler {
    @Override
    public boolean handle(Event event) throws Throwable {
        //与移动合作，送100块充值卡
        return true;
    }
}
