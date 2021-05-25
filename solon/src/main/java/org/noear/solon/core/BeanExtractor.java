package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Bean 提取器（提取函数，此类用于扩展AopContext，为其添加提取器）
 *
 * @author noear
 * @since 1.4
 */
@FunctionalInterface
public interface BeanExtractor<T extends Annotation> {
    /**
     * 提取
     * */
    void doExtract(BeanWrap bw, Method method, T anno);
}
