package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Bean 提取器（函数）
 *
 * @author noear
 * @since 1.4
 */
public interface BeanExtractor<T extends Annotation> {
    /**
     * 提取
     * */
    void doExtract(BeanWrap wrap, Method method, T anno);
}
