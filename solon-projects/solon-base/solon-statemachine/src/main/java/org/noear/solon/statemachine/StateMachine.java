package org.noear.solon.statemachine;

import java.util.ArrayList;
import java.util.List;

public class StateMachine<S extends State, E extends Event, T> {
    /**
     * 当前状态
     */
    private S currentState;

    private final List<StateTransition<S, E, T>> transitions = new ArrayList<>();

    public StateMachine(S initState) {
        this.currentState = initState;
    }

    /**
     * 添加状态转换规则
     */
    public void addTransition(StateTransition<S, E, T> transition) {
        transitions.add(transition);
    }

    public synchronized boolean execute(E event, T payload) {
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
    }

    public synchronized boolean execute(E event) {
        return execute(event, null);

    }

    public S getCurrentState() {
        return currentState;
    }

    public void restore(S state) {
        this.currentState = state;
    }
}