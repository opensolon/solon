package org.noear.solon.core;

public interface EventHandler<Event> {
    void handle(XContext ctx, Event event);
}
