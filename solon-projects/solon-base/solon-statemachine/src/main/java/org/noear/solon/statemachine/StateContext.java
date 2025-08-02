package org.noear.solon.statemachine;

/**
 * 状态机上下文，保存状态转换相关信息
 */
public class StateContext<S extends State, E extends Event, T> {
    private final S from;
    private final S to;
    private final E event;
    private final T payload;

    public StateContext(S from, S to, E event, T payload) {
        this.from = from;
        this.to = to;
        this.event = event;
        this.payload = payload;
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


    public T getPayload() {
        return payload;
    }

}