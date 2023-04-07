package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;

/**
 * @author orangej
 * @since 2023/4/7
 */
public interface IAsyncInvocationRunnableFactory {
    Runnable create(Invocation inv);
}
