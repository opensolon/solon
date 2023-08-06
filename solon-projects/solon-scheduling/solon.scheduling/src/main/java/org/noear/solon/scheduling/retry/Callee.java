package org.noear.solon.scheduling.retry;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 被调用者
 *
 * @author noear
 * @since 2.4
 */
public interface Callee {
    /**
     * 被调目标
     */
    Object target();

    /**
     * 被调函数
     */
    Method method();

    /**
     * 参数
     */
    Object args();

    /**
     * 参数 map 形式
     */
    Map<String, Object> argsAsMap();

    /**
     * 调用
     */
    Object call() throws Throwable;
}
