package demo.eventplus;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudEventEntity;
import org.noear.solon.cloud.CloudEventEntityHandler;
import org.noear.solon.cloud.annotation.CloudEvent;

/**
 * @author noear 2021/11/5 created
 */
public class Demo {
    //实体
    @CloudEvent("user.create.event")
    public class UserCreatedEvent implements CloudEventEntity {
        public long userId;
    }

    //订阅（实体已申明主题相关信息）
    @CloudEvent
    public class UserCreatedEventHandler implements CloudEventEntityHandler<UserCreatedEvent>{
        @Override
        public boolean handler(UserCreatedEvent event) throws Throwable {
            //业务处理

            return false;
        }
    }

    //发送
    public void publishDemo(){
        UserCreatedEvent event = new UserCreatedEvent();
        event.userId  =1212;

        CloudClient.event().publish(event);
    }
}
