package demo.controller;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear 2022/11/21 created
 */
@CloudEvent("demo.event1")
public class Event1 implements CloudEventHandler {
    @Override
    public boolean handle(Event event) throws Throwable {
        System.out.println("事件打印: " + event);
        return true;
    }
}
