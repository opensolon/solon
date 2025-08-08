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
package org.noear.solon.statemachine;

import org.noear.solon.core.util.Assert;
import org.noear.solon.lang.NonNull;
import org.noear.solon.lang.Preview;

/**
 * 状态机上下文，保存状态转换相关信息
 *
 * @author 王奇奇
 * @serial 3.4
 */
@Preview("3.4")
public class StateContext<S extends State, E extends Event, T> {
    /**
     * 起始状态。即从来哪里来
     */
    private final S from;
    /**
     * 下一步状态。即到哪里去
     */
    private final S to;
    /**
     * 从起始状态到下一步状态的过程中执行的事件。逻辑事件，非具体执行内容
     */
    private final E event;
    /**
     * 业务负载对象
     */
    private final T payload;

    public StateContext(S from, S to, @NonNull E event, T payload) {
        Assert.notNull(from, "new StateContext时form不能为null");
        Assert.notNull(to, "new StateContext时to不能为null");
        Assert.notNull(event, "new StateContext时event不能为null");

        this.from = from;
        this.to = to;
        this.event = event;
        this.payload = payload;
    }

    public S getFrom() {
        return from;
    }

    public S getTo() {
        return to;
    }

    public E getEvent() {
        return event;
    }

    public T getPayload() {
        return payload;
    }
}