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

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * 状态转换定义
 *
 * @author 王奇奇
 * @serial 3.4
 */
@Preview("3.4")
public class StateTransition<S extends State, E extends Event, T> {
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
     * 条件判断，满足条件时才执行
     */
    private final BiPredicate<StateContext<S, E, T>, T> condition;
    /**
     * 具体执行的内容
     */
    private final Consumer<StateContext<S, E, T>> action;

    public StateTransition(S from, S to, E event) {
        this(from, to, event, null, null);
    }

    public StateTransition(S from, S to, E event, Consumer<StateContext<S, E, T>> action) {
        this(from, to, event, null, action);
    }

    public StateTransition(S from, S to, E event, BiPredicate<StateContext<S, E, T>, T> condition, Consumer<StateContext<S, E, T>> action) {
        Assert.notNull(from, "The from cannot be null");
        Assert.notNull(to, "The to cannot be null");
        Assert.notNull(event, "The event cannot be null");

        this.from = from;
        this.to = to;
        this.event = event;

        if (condition == null) {
            this.condition = (ctx, payload) -> true;
        } else {
            this.condition = condition;
        }

        if (action == null) {
            this.action = (c) -> {
            };
        } else {
            this.action = action;
        }
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

    /**
     * 判断状态机定义是否满足条件
     *
     * @param state   当前状态
     * @param event   当前时间
     * @param payload 负载
     * @return 是否
     */
    public boolean matches(S state, E event, T payload) {
        return from.equals(state) && this.event.equals(event) && condition.test(new StateContext<>(from, to, event, payload), payload);
    }

    /**
     * 执行事件
     *
     * @param context 状态机上下文
     */
    public void execute(StateContext<S, E, T> context) {
        action.accept(context);
    }
}