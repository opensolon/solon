package org.noear.solon.cloud.extend.rocketmq.test;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

import java.util.Date;

/**
 * @author noear 2022/12/12 created
 */
public class Demo {
    //消费示例
    @CloudEvent(topic = "demo_user", tag = "on_register")
    public class Event_demo_user_on_register implements CloudEventHandler {
        @Override
        public boolean handle(Event event) throws Throwable {
            return false;
        }
    }

    public class UserService{
        public void register(long userId){
            //..业务处理

            //发送注册事件
            Event event = new Event("demo_user", String.valueOf(userId))
                    .tags("on_register");

            CloudClient.event().publish(event);
        }

        public void register2(long userId){
            //..业务处理

            //发送注册事件，并定时
            Event event = new Event("demo_user", String.valueOf(userId))
                    .tags("on_register")
                    .scheduled(new Date(System.currentTimeMillis() + 30 *1000)); //30秒后执行

            CloudClient.event().publish(event);
        }
    }
}
