package org.noear.solon.core.handle;

/**
 * @author noear
 * @since 2.7
 */
public interface HandlerLoader {
    String mapping();
    void load(HandlerSlots slots);
}
