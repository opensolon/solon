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

/**
 * 状态转换器
 *
 * @author 王奇奇
 * @serial 3.4
 */
@Preview("3.4")
public class StateTransition<S extends State, E extends Event, T> {
    private StateTransitionDecl<S, E, T> decl;

    public StateTransition(StateTransitionDecl<S, E, T> decl) {
        this.decl = decl;
    }

    protected S getTo() {
        return decl.target;
    }

    /**
     * 匹配
     *
     * @param event   事件
     * @param eventContext 事件上下文
     * @return 是否
     */
    protected boolean matches(E event, EventContext<S, T> eventContext) {
        return decl.event.equals(event) &&
                decl.source.contains(eventContext.getCurrentState()) &&
                decl.condition.test(new StateTransitionContext<>(eventContext.getCurrentState(), decl.target, event, eventContext.getPayload()));
    }

    /**
     * 执行
     *
     * @param context 状态机上下文
     */
    protected void execute(StateTransitionContext<S, E, T> context) {
        decl.action.accept(context);
    }
}