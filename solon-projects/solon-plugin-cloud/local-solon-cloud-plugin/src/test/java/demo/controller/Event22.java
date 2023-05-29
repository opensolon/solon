package demo.controller;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件（本地实现时，不支持ACK，不支持延时；最好还是引入消息队列的适配框架）
 */
@CloudEvent("demo.event22")
public class Event22 implements CloudEventHandler {
    @Override
    public boolean handle(Event event) throws Throwable {
        System.out.println("云端事件打印: " + ONode.stringify(event));

        if (event.times() > 2) {
            return true;
        } else {
            throw new IllegalAccessException("次数未到...: " + event.times());
        }
    }
}
