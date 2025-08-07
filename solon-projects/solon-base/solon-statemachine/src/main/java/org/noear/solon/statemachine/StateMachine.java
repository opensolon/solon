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
     * 当前状态
     */
    private S currentState;

    /**
     * 转换器
     *
     */
    private final List<StateTransition<S, E, T>> transitions = new ArrayList<>();

    /**
     * 同步锁
     *
     */
    private final ReentrantLock LOCKER = new ReentrantLock();

    public StateMachine(S initState) {
        this.currentState = initState;
    }

    /**
     * 添加状态转换规则
     */
    public void addTransition(StateTransition<S, E, T> transition) {
        transitions.add(transition);
    }

    /**
     * 执行
     *
     */
    public boolean execute(E event, T payload) {
        LOCKER.lock();

        try {
            for (StateTransition<S, E, T> transition : transitions) {
                if (transition.matches(currentState, event, payload)) {
                    S from = currentState;
                    S to = transition.getTo();
                    currentState = to;
                    transition.execute(new StateContext<>(from, to, event, payload));
                    return true;
                }
            }
            return false;
        } finally {
            LOCKER.unlock();
        }
    }

    /**
     * 执行
     */
    public boolean execute(E event) {
        return execute(event, null);

    }

    /**
     * 获取当前状态
     *
     */
    public S getCurrentState() {
        return currentState;
    }

    /**
     * 重置状态
     *
     */
    public void restore(S state) {
        this.currentState = state;
    }
}