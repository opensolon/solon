package org.noear.solon.core;

public interface XEventHandler<Event> {
    void handle(XContext ctx, Event event);
}
