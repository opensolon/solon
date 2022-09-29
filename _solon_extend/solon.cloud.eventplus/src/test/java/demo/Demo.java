package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.eventplus.CloudEventEntity;
import org.noear.solon.cloud.eventplus.CloudEventHandlerPlus;
import org.noear.solon.cloud.eventplus.CloudEventSubscribe;

/**
 * @author noear 2021/11/5 created
 */
public class Demo {
    //实体
    @CloudEvent("user.create.event")
    public class UserCreatedEvent implements CloudEventEntity {
        public long userId;
    }
    //代理模式订阅（实体已申明主题相关信息）
    @CloudEventSubscribe
    public class UserCreatedEventHandler implements CloudEventHandlerPlus<UserCreatedEvent> {
        @Override
        public boolean handle(UserCreatedEvent event) throws Throwable {
            //业务处理
            return false;
        }
    }
    //类函数模式订阅
    @Component
    public class EventSubscriber{
        @CloudEventSubscribe
        public boolean onUserCreatedEvent(UserCreatedEvent event){
            //处理业务
            return false;
        }
    }
    //发送
    public void publishDemo(){
        UserCreatedEvent event = new UserCreatedEvent();
        event.userId  =1212;
        event.publish();
    }
}
