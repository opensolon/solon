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
    public boolean handle(Event event) throws Throwable {
        //送2块金币
        return true;
    }
}
