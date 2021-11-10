package demo;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * 同一个事件主题，支持多个本地订阅。可以做不同业务的领域隔离
 *
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
