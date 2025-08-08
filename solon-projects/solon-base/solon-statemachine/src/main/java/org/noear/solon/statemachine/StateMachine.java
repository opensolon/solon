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

/**
 * 状态机
 *
 * @author 王奇奇
 * @serial 3.4
 */
@Preview("3.4")
public class StateMachine<S extends State, E extends Event, T> {
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
    public void addTransition(StateTransition<S, E, T> transition) {
        Assert.notNull(transition,"StateMachine.addTransition时transition不能为null");
        transitions.add(transition);
    }

    /**
     * 执行
     */
    public S execute(S currentState, E event, T payload) {
        Assert.notNull(currentState,"StateMachine.execute时currentState不能为null");
        Assert.notNull(event,"StateMachine.execute时event不能为null");
        LOCKER.lock();

        try {
            for (StateTransition<S, E, T> transition : transitions) {
                if (transition.matches(currentState, event, payload)) {
                    S to = transition.getTo();
                    transition.execute(new StateContext<>(currentState, to, event, payload));
                    return currentState;
                }
            }
            throw new IllegalStateException("无法从状态[" + currentState + "] 通过事件[" + event + "] 找到有效的状态机");
        } finally {
            LOCKER.unlock();
        }
    }

    /**
     * 执行
     */
    public S execute(S currentState, E event) {
        return execute(currentState, event, null);
    }
}