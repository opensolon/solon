package org.noear.solon.core;

import java.lang.annotation.Annotation;

public interface BeanCreator<T extends Annotation> {
    void handler(Class<?> clz, BeanWrap wrap, T anno) throws Exception;
}
