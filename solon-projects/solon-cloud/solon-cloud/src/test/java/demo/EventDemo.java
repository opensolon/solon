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

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.data.annotation.Transaction;

/**
 * @author noear 2024/5/14 created
 */
public class EventDemo {
    public void event_only() {
        EventTran eventTran = CloudClient.event().newTran();

        try {
            CloudClient.event().publish(new Event("demo.event", "test1").tran(eventTran));
            CloudClient.event().publish(new Event("demo.event", "test2").tran(eventTran));
            CloudClient.event().publish(new Event("demo.event", "test3").tran(eventTran));

            eventTran.commit();
        } catch (Throwable ex) {
            eventTran.rollback();
        }
    }

    @Transaction
    public void event_and_jdbc() {
        EventTran eventTran = CloudClient.event().newTranAndJoin(); //加入 @Tran 管理

        CloudClient.event().publish(new Event("demo.event", "test1").tran(eventTran));
        CloudClient.event().publish(new Event("demo.event", "test2").tran(eventTran));
        CloudClient.event().publish(new Event("demo.event", "test3").tran(eventTran));
    }
}