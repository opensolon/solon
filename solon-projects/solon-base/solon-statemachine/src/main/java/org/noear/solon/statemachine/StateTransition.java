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
    private final S from;
    private final S to;
    private final E event;

    private final BiPredicate<StateContext<S, E, T>, T> condition;
    private final Consumer<StateContext<S, E, T>> action;

    public StateTransition(S from, S to, E event) {
        this(from, to, event, (ctx, payload) -> true, (ctx) -> {
        });
    }

    public StateTransition(S from, S to, E event, Consumer<StateContext<S, E, T>> action) {
        this(from, to, event, (ctx, payload) -> true, action);
    }

    public StateTransition(S from, S to, E event, BiPredicate<StateContext<S, E, T>, T> condition, Consumer<StateContext<S, E, T>> action) {
        this.from = from;
        this.to = to;
        this.event = event;
        this.condition = condition;
        this.action = action;
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

    public boolean matches(S state, E event, T payload) {
        return from.equals(state) && this.event.equals(event) && condition.test(new StateContext<>(from, to, event, payload), payload);
    }

    public void execute(StateContext<S, E, T> context) {
        action.accept(context);
    }

}