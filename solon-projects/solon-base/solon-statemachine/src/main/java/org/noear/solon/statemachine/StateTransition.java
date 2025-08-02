package org.noear.solon.statemachine;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * 状态转换定义
 */
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