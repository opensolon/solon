package org.noear.solon.core;

import java.lang.annotation.Annotation;

/**
 * Bean 注入处理器
 * */
public interface BeanInjector<T extends Annotation> {
    void handler(FieldWrapTmp fwT, T anno);
}
