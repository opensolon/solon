package org.noear.solon.extend.hook;

import java.util.Map;

/**
 * @author noear
 * @since 1.8
 */
public interface HookHandler {
    void handle(Map<String, Object> args);
}
