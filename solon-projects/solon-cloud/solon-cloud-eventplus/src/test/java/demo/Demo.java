/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
