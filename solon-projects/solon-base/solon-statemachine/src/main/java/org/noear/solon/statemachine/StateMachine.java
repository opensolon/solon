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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 状态机
 *
 * @author 王奇奇
 * @author noear
 * @serial 3.4
 * @param <S> 状态
 * @param <E> 事件
 * @param <T> 装载
 */
@Preview("3.4")
public class StateMachine<S, E, T> {
    /**
     * 转换器
     */
    private final List<StateTransition<S, E, T>> transitions = new ArrayList<>();

    /**
     * 同步锁
     */
    private final ReentrantLock LOCKER = new ReentrantLock();

    public StateMachine() {
    }

    /**
     * 添加状态转换规则
     */
    public void addTransition(Consumer<StateTransitionDecl<S, E, T>> declaration) {
        Assert.notNull(declaration, "The declaration cannot be null");

        StateTransitionDecl<S, E, T> decl = new StateTransitionDecl<>();
        declaration.accept(decl);
        decl.check();//检测

        transitions.add(new StateTransition<>(decl));
    }

    /**
     * 添加状态转换规则
     */
    protected StateTransitionDecl<S, E, T> from(S... source) {
        StateTransitionDecl<S, E, T> decl = new StateTransitionDecl<>();
        decl.from(source);
        transitions.add(new StateTransition<>(decl));
        return  decl;
    }

    /**
     * 发送事件
     *
     * @param event        事件
     * @param eventContext 事件上下文
     */
    public S sendEvent(E event, EventContext<S, T> eventContext) {
        Assert.notNull(event, "The event cannot be null");
        Assert.notNull(eventContext, "The eventContext cannot be null");

        LOCKER.lock();

        try {
            for (StateTransition<S, E, T> transition : transitions) {
                if (transition.matches(event, eventContext)) {
                    S to = transition.getTo();
                    transition.execute(new StateTransitionContext<>(eventContext.getCurrentState(), to, event, eventContext.getPayload()));
                    return to;
                }
            }
            throw new IllegalStateException("Unable to transition from state '" + eventContext.getCurrentState() + "' and event '" + event + "' to a valid new state");
        } finally {
            LOCKER.unlock();
        }
    }
}