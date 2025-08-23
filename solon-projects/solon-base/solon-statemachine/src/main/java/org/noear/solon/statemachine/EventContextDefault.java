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
import org.noear.solon.lang.Preview;

/**
 * 事件上下文默认实现
 *
 * @author noear
 * @since 3.4
 * @param <S> 状态
 * @param <T> 装载
 */
@Preview("3.4")
public class EventContextDefault<S, T> implements EventContext<S,T> {
    private S currentState;
    private T payload;

    /**
     * 获取当前状态
     *
     */
    public S getCurrentState() {
        return currentState;
    }

    /**
     * 获取装载
     *
     */
    public T getPayload() {
        return payload;
    }

    protected EventContextDefault(S currentState, T payload) {
        Assert.notNull(currentState, "The currentState cannot be null");

        this.currentState = currentState;
        this.payload = payload;
    }
}