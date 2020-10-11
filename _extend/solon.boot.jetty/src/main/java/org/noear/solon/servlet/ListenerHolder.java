package org.noear.solon.servlet;

import java.util.EventListener;

public class ListenerHolder {
    private EventListener listener;

    public ListenerHolder(EventListener listener) {
        this.listener = listener;
    }

    public EventListener getListener() {
        return listener;
    }
}
