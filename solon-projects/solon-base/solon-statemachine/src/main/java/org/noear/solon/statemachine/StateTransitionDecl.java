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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 状态转换定义
 *
 * @author noear
 * @since 3.4
 * @param <S> 状态
 * @param <E> 事件
 * @param <T> 装载
 */
@Preview("3.4")
public class StateTransitionDecl<S, E, T> {
    /**
     * 来源状态（即从来哪里来）
     */
    protected List<S> source;
    /**
     * 目标状态（即到哪里去）
     */
    protected S target;
    /**
     * 触发事件（触发转换）
     */
    protected E event;
    /**
     * 额外条件，触发后的进一步条件需求
     */
    protected Predicate<StateTransitionContext<S, E, T>> condition =  (c) -> true;

    /**
     * 具体执行的内容
     */
    protected Consumer<StateTransitionContext<S, E, T>> action = (c) -> {
    };

    /**
     * 检测
     *
     */
    public void check() {
        Assert.notNull(source, "The source state(from) cannot be null");
        Assert.notNull(target, "The target state(to) cannot be null");
        Assert.notNull(event, "The on(event) cannot be null");
    }

    /**
     * 来源状态
     *
     */
    public StateTransitionDecl<S, E, T> from(S... source) {
        Assert.notNull(source, "The source state(from) cannot be null");

        this.source = Arrays.asList(source);
        return this;
    }

    /**
     * 目标状态
     */
    public StateTransitionDecl<S, E, T> to(S target) {
        Assert.notNull(target, "The target state(to) cannot be null");

        this.target = target;
        return this;
    }

    /**
     * 触发事件
     */
    public StateTransitionDecl<S, E, T> on(E event) {
        Assert.notNull(event, "The on(event) cannot be null");

        this.event = event;
        return this;
    }

    /**
     * 额外条件
     */
    public StateTransitionDecl<S, E, T> when(Predicate<StateTransitionContext<S, E, T>> condition) {
        this.condition = condition;
        return this;
    }

    /**
     * 然后执行动作
     */
    public StateTransitionDecl<S, E, T> then(Consumer<StateTransitionContext<S, E, T>> action) {
        this.action = action;
        return this;
    }
}