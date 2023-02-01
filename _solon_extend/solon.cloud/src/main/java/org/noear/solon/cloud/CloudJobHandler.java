package org.noear.solon.cloud;

import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
@FunctionalInterface
public interface CloudJobHandler {
    void handle(Context ctx) throws Throwable;
}
