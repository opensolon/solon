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
package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.data.tran.TranUtils;

/**
 * 云端事件服务（事件总线服务）
 *
 * @author noear
 * @since 1.2
 */
public interface CloudEventService {
    /**
     * 新建事务
     */
    default EventTran newTran(){
        return new EventTran();
    }

    /**
     * 新建事务并加入`@Tran`注解管理
     */
    default EventTran newTranAndJoin() {
        EventTran tran = newTran();
        if (TranUtils.inTrans()) {
            TranUtils.listen(tran);
        }

        return tran;
    }

    /**
     * 发布事件
     *
     * @param event 事件
     */
    boolean publish(Event event) throws CloudEventException;

    /**
     * 关注事件（相当于订阅）
     *
     * @param level    事件级别
     * @param channel  通道
     * @param group    分组
     * @param topic    主题
     * @param tag      标签
     * @param qos      服务质量
     * @param observer 观察者
     */
    void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer);
}