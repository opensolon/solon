package org.noear.solon.scheduling.async;

import org.noear.solon.core.aspect.Invocation;

/**
 * 调用运行器工厂
 *
 * @author orangej
 * @since 2.2
 */
public interface InvocationRunnableFactory {
    /**
     * 创建调用运行器
     * */
    Runnable create(Invocation inv);
}
