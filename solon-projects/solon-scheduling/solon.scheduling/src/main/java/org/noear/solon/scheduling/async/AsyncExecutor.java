package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * 异步执行器
 *
 * @author noear
 * @since 2.4
 */
public interface AsyncExecutor {
    /**
     * 提交执行
     *
     * @param inv  调用者
     * @param anno 注解
     */
    Future submit(Invocation inv, Async anno) throws Throwable;
}
