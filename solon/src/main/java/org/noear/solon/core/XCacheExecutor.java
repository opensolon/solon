package org.noear.solon.core;

import org.noear.solon.annotation.XCache;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.Parameter;

public interface XCacheExecutor {
    /**
     * 执行缓存
     */
    Object execute(XCache anno, Parameter[] params, Object[] values, RunnableEx runnable) throws Throwable;
}
