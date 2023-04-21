package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;

/**
 * 任务处理器
 *
 * @author noear
 * @since 2.2
 */
@FunctionalInterface
public interface JobHandler {
    void handle(Context ctx) throws Throwable;
}
