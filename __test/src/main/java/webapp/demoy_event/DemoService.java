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
package webapp.demoy_event;

import lombok.Getter;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Managed;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.event.EventListener;

/**
 * @author noear 2022/5/18 created
 */
@Managed
public class DemoService {
    public void hello(String name){
        //发布事件
        EventBus.publish(new HelloEvent(name));
    }

    //定义事件模型
    @Getter
    public static class HelloEvent {
        private String name;
        public HelloEvent(String name){
            this.name = name;
        }
    }

    //监听事件并处理
    @Managed
    public static class HelloEventListener implements EventListener<HelloEvent> {
        @Override
        public void onEvent(HelloEvent event) throws Throwable {
            event.getName();
        }
    }
}
