package org.noear.solon.core;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.ext.SupplierEx;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface XCacheExecutor {
    /**
     * 执行缓存
     */
    Object cachePut(XCachePut anno, Method method, Parameter[] params, Object[] values, SupplierEx callable) throws Throwable;

    void cacheRemove(XCacheRemove anno, Method method, Parameter[] params, Object[] values);
}
