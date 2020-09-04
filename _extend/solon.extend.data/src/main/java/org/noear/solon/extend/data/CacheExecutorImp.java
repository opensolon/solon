package org.noear.solon.extend.data;

import org.noear.solon.annotation.XCache;
import org.noear.solon.core.XCacheExecutor;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.Parameter;

/**
 * 缓存执行器
 * */
public class CacheExecutorImp implements XCacheExecutor {
    @Override
    public Object execute(XCache anno, Parameter[] params, Object[] values, RunnableEx runnable) throws Throwable {
        return null;
    }
}
